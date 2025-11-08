package familyConnection.domain.comment;

import familyConnection.domain.answer.Answer;
import familyConnection.domain.user.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "comment_id")
  @org.hibernate.annotations.Comment("댓글 아이디")
  private Long commentId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "answer_id", nullable = false)
  @org.hibernate.annotations.Comment("답변")
  private Answer answer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @org.hibernate.annotations.Comment("사용자")
  private User user;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  @org.hibernate.annotations.Comment("댓글 내용")
  private String content;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  @org.hibernate.annotations.Comment("생성시각")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  @org.hibernate.annotations.Comment("수정시각")
  private LocalDateTime updatedAt;

  @Column(name = "is_deleted", nullable = false)
  @Builder.Default
  @org.hibernate.annotations.Comment("삭제 여부")
  private Boolean isDeleted = false;
}
