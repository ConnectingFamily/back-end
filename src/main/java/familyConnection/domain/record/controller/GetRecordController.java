package familyConnection.domain.record.controller;

import familyConnection.domain.family.repository.FamilyMemberRepository;
import familyConnection.domain.record.dto.MonthlyRecordResponseDto;
import familyConnection.domain.record.service.RecordService;
import familyConnection.global.apiPayload.ApiResponse;
import familyConnection.global.apiPayload.code.status.ErrorStatus;
import familyConnection.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
@Tag(name = "Record", description = "답변 기록 조회 API")
public class GetRecordController {
    private final RecordService recordService;
    private final FamilyMemberRepository familyMemberRepository;  // 추가

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }

    private Long getCurrentFamilyId(Long userId) {
        return familyMemberRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus._FAMILY_NOT_FOUND))
                .getFamily()
                .getFamilyId();
    }

    @Operation(
            summary = "답변 기록 조회 (달력)",
            description = "내가 속한 가족의 해당 연/월 답변 날짜들을 자동으로 조회합니다. " +
                    "familyId를 프론트에서 넘길 필요가 없습니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<MonthlyRecordResponseDto>> getMonthlyRecords(
            @RequestParam int year,
            @RequestParam int month
    ) {
        Long userId = getCurrentUserId();
        Long familyId = getCurrentFamilyId(userId); // ⭐ 자동 조회

        MonthlyRecordResponseDto dto = recordService.getMonthlyRecords(userId, familyId, year, month);
        return ResponseEntity.ok(ApiResponse.onSuccess(dto));
    }
}

