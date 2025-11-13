package familyConnection.domain.comment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentType {
  TEXT("텍스트 댓글"),
  EMOJI("이모지 댓글"),
  BOTH("텍스트와 이모지 댓글");

  private final String description;
}
