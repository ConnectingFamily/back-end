package familyConnection.domain.record.controller;

import familyConnection.domain.record.dto.MonthlyRecordResponseDto;
import familyConnection.domain.record.dto.MyAnsweredQuestionListDto;
import familyConnection.domain.record.dto.RecordDetailResponseDto;
import familyConnection.domain.record.dto.RecordListResponseDto;
import familyConnection.domain.record.service.RecordService;
import familyConnection.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/families/{familyId}/records")
@RequiredArgsConstructor
@Tag(name = "Record", description = "답변 기록 조회 API")
public class RecordController {

    private final RecordService recordService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }


    //TODO 삭제해야할 것.
    @Operation(summary = "답변 기록 조회 (달력)", description = "해당 가족, 해당 연/월에 내가 답변한 날짜들을 반환합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<MonthlyRecordResponseDto>> getMonthlyRecords(
            @PathVariable Long familyId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        Long userId = getCurrentUserId();
        MonthlyRecordResponseDto dto = recordService.getMonthlyRecords(userId, familyId, year, month);
        return ResponseEntity.ok(ApiResponse.onSuccess(dto));
    }

    @Operation(summary = "내가 답변한 질문 목록", description = "선택한 가족에서 내가 답했던 질문들을 최신순으로 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<MyAnsweredQuestionListDto>> getMyAnsweredQuestions(
            @PathVariable Long familyId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        Long userId = getCurrentUserId();
        MyAnsweredQuestionListDto dto = recordService.getMyAnsweredQuestions(userId, familyId, page, limit);
        return ResponseEntity.ok(ApiResponse.onSuccess(dto));
    }

    @Operation(summary = "특정 답변 상세 조회", description = "daily_question_id 기준으로 내가 그날 작성한 답변과 질문 내용을 함께 조회합니다.")
    @GetMapping("/{dailyQuestionId}")
    public ResponseEntity<ApiResponse<RecordDetailResponseDto>> getRecordDetail(
            @PathVariable Long familyId,
            @PathVariable Long dailyQuestionId
    ) {
        Long userId = getCurrentUserId();
        RecordDetailResponseDto dto = recordService.getRecordDetail(userId, familyId, dailyQuestionId);
        return ResponseEntity.ok(ApiResponse.onSuccess(dto));
    }
}
