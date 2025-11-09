package familyConnection.domain.aianswerhelper.controller;


import familyConnection.domain.aianswerhelper.dto.AnswerDraftRequestDto;
import familyConnection.domain.aianswerhelper.dto.AnswerDraftResponseDto;
import familyConnection.domain.aianswerhelper.dto.AnswerFinalizeRequestDto;
import familyConnection.domain.aianswerhelper.service.AnswerSubmissionService;
import familyConnection.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Answer Submission", description = "일일 질문에 대한 초안 및 최종 답변 제출 API")
@RestController
@RequestMapping("/api/daily-questions")
@RequiredArgsConstructor
public class AnswerSubmissionController {

    private final AnswerSubmissionService answerSubmissionService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }

    @Operation(summary = "초안 제출 + AI 피드백 요청")
    @PostMapping("/{dailyQuestionId}/answers/draft")
    public ResponseEntity<ApiResponse<AnswerDraftResponseDto>> createDraft(
            @PathVariable Long dailyQuestionId,
            @RequestBody AnswerDraftRequestDto request
    ) {
        Long userId = getCurrentUserId();
        AnswerDraftResponseDto dto = answerSubmissionService.createDraftAnswer(userId, dailyQuestionId, request);
        return ResponseEntity.ok(ApiResponse.onSuccess(dto));
    }

    @Operation(summary = "최종 답변 제출")
    @PatchMapping("/{dailyQuestionId}/answers")
    public ResponseEntity<ApiResponse<Void>> finalizeAnswer(
            @PathVariable Long dailyQuestionId,
            @RequestBody AnswerFinalizeRequestDto request
    ) {
        Long userId = getCurrentUserId();
        answerSubmissionService.finalizeAnswer(userId, dailyQuestionId, request);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}