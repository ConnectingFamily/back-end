package familyConnection.domain.reaction.controller;

import familyConnection.domain.reaction.dto.ReactionRequestDto;
import familyConnection.domain.reaction.dto.ReactionResponseDto;
import familyConnection.domain.reaction.service.ReactionService;
import familyConnection.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Reaction", description = "답변/댓글에 대한 이모지 반응 API")
@RestController
@RequestMapping("/api/v1/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }

    @Operation(summary = "반응 추가", description = "대상 타입(ANSWER/COMMENT)과 대상 ID에 대해 이모지 반응을 추가합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ReactionResponseDto>> addReaction(
            @RequestBody ReactionRequestDto request
    ) {
        Long userId = getCurrentUserId();
        ReactionResponseDto dto = reactionService.addReaction(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(dto));
    }

    @Operation(summary = "반응 취소", description = "Reaction ID로 반응을 취소합니다.")
    @DeleteMapping("/{reactionId}")
    public ResponseEntity<ApiResponse<Void>> removeReaction(
            @PathVariable Long reactionId
    ) {
        Long userId = getCurrentUserId();
        reactionService.removeReaction(userId, reactionId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }


    @Operation(summary = "반응 조회", description = "특정 대상(ANSWER/COMMENT)에 달린 모든 반응을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReactionResponseDto>>> getReactions(
            @RequestParam("target_type") String targetType,
            @RequestParam("target_id") Long targetId
    ) {
        List<ReactionResponseDto> reactions = reactionService.getReactions(targetType, targetId);
        return ResponseEntity.ok(ApiResponse.onSuccess(reactions));
    }
}
