package familyConnection.domain.comment.controller;

import familyConnection.domain.comment.dto.CommentCreateRequestDto;
import familyConnection.domain.comment.dto.CommentResponseDto;
import familyConnection.domain.comment.dto.CommentUpdateRequestDto;
import familyConnection.domain.comment.service.CommentService;
import familyConnection.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "답변에 달리는 댓글 등록/수정/삭제 API")
public class CommentController {

        private final CommentService commentService;

        private Long getCurrentUserId() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                return Long.parseLong(authentication.getName());
        }

        @Operation(summary = "댓글 조회", description = "특정 일일 질문(dailyQuestionId)에 달린 댓글 목록을 조회합니다.")
        @GetMapping
        public ResponseEntity<ApiResponse<List<CommentResponseDto>>> getComments(
                        @Parameter(description = "일일 질문 ID", example = "1", required = true) @RequestParam Long dailyQuestionId) {
                List<CommentResponseDto> comments = commentService.getCommentsByDailyQuestionId(dailyQuestionId);
                return ResponseEntity.ok(ApiResponse.onSuccess(comments));
        }

        @Operation(summary = "댓글 등록", description = "특정 일일 질문(dailyQuestionId)에 댓글을 등록합니다.")
        @PostMapping
        public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
                        @RequestBody CommentCreateRequestDto request) {
                Long userId = getCurrentUserId();
                CommentResponseDto dto = commentService.createComment(userId, request);
                return ResponseEntity.ok(ApiResponse.onSuccess(dto));
        }

        @Operation(summary = "댓글 수정", description = "내가 작성한 댓글의 내용을 수정합니다.")
        @PutMapping("/{commentId}")
        public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
                        @Parameter(description = "댓글 ID", example = "1") @PathVariable Long commentId,
                        @RequestBody CommentUpdateRequestDto request) {
                Long userId = getCurrentUserId();
                CommentResponseDto dto = commentService.updateComment(userId, commentId, request);
                return ResponseEntity.ok(ApiResponse.onSuccess(dto));
        }

        @Operation(summary = "댓글 삭제", description = "내가 작성한 댓글을 삭제합니다. 실제로는 is_deleted=true로 소프트 삭제됩니다.")
        @DeleteMapping("/{commentId}")
        public ResponseEntity<ApiResponse<Void>> deleteComment(
                        @Parameter(description = "댓글 ID", example = "1") @PathVariable Long commentId) {
                Long userId = getCurrentUserId();
                commentService.deleteComment(userId, commentId);
                return ResponseEntity.ok(ApiResponse.onSuccess(null));
        }
}
