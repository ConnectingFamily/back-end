package familyConnection.domain.question.controller;

import familyConnection.domain.question.dto.AnswerDetailDto;
import familyConnection.domain.question.dto.AnswerRequestDto;
import familyConnection.domain.question.dto.DailyQuestionResponseDto;
import familyConnection.domain.question.service.DailyQuestionService;
import familyConnection.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Daily Question", description = "가족별 일일 질문 조회/답변 API")
@RestController
@RequestMapping("/api/daily-questions")
@RequiredArgsConstructor
public class DailyQuestionController {

    private final DailyQuestionService dailyQuestionService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }

    @Operation(
            summary = "오늘의 질문 조회",
            description = "로그인한 사용자가 속한 가족에 오늘 할당된 질문을 조회합니다. " +
                    "응답에는 가족 구성원별 답변 여부도 포함되며, 모든 가족이 답변을 완료한 경우에만 다른 사람의 답변 내용이 공개됩니다."
    )
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<DailyQuestionResponseDto>> getTodayQuestion() {
        Long userId = getCurrentUserId();
        DailyQuestionResponseDto dto = dailyQuestionService.getTodayQuestion(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(dto));
    }

    @Operation(
            summary = "오늘 질문에 답변 등록",
            description = "해당 일일 질문(dailyQuestionId)에 대한 나의 답변을 저장합니다. " +
                    "하루에 한 번만 등록할 수 있으며, 모든 가족이 답변을 마치면 질문이 공개됩니다."
    )
    @PostMapping("/{dailyQuestionId}/answers")
    public ResponseEntity<ApiResponse<Void>> saveAnswer(
            @Parameter(description = "일일 질문 ID", example = "1")
            @PathVariable Long dailyQuestionId,
            @RequestBody AnswerRequestDto request
    ) {
        Long userId = getCurrentUserId();
        dailyQuestionService.saveAnswer(userId, dailyQuestionId, request);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(
            summary = "가족 답변 목록 조회",
            description = "같은 가족이 오늘 질문에 남긴 답변을 조회합니다. " +
                    "아직 모든 가족이 답변하지 않았다면 본인 답변만 내용이 보이며, 나머지는 answered 여부만 노출됩니다."
    )
    @GetMapping("/{dailyQuestionId}/answers")
    public ResponseEntity<ApiResponse<List<AnswerDetailDto>>> getAnswers(
            @Parameter(description = "일일 질문 ID", example = "1")
            @PathVariable Long dailyQuestionId
    ) {
        Long userId = getCurrentUserId();
        List<AnswerDetailDto> answers = dailyQuestionService.getAnswers(userId, dailyQuestionId);
        return ResponseEntity.ok(ApiResponse.onSuccess(answers));
    }

    @Operation(
            summary = "오늘 질문에 대한 내 답변 삭제",
            description = "해당 일일 질문(dailyQuestionId)에 대해 내가 작성한 답변을 삭제합니다. " +
                    "삭제 후에는 다시 답변을 작성할 수 있습니다."
    )
    @DeleteMapping("/{dailyQuestionId}/answers")
    public ResponseEntity<ApiResponse<Void>> deleteMyAnswer(
            @Parameter(description = "일일 질문 ID", example = "1")
            @PathVariable Long dailyQuestionId
    ) {
        Long userId = getCurrentUserId();
        dailyQuestionService.deleteMyAnswer(userId, dailyQuestionId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }


}
