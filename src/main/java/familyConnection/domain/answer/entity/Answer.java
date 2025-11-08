package familyConnection.domain.answer.entity;

import familyConnection.domain.family.entity.Family;
import familyConnection.domain.question.entity.DailyQuestion;
import familyConnection.domain.user.entity.User;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "answers",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_answers_daily_question_user", columnNames = {"daily_question_id", "user_id"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    @Comment("답변 아이디")
    private Long answerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_question_id", nullable = false)
    @Comment("일일 질문")
    private DailyQuestion dailyQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("사용자")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    @Comment("가족")
    private Family family;

    @Column(name = "original_answer", nullable = false, columnDefinition = "TEXT")
    @Comment("원본 답변")
    private String originalAnswer;

    @Column(name = "final_answer", nullable = false, columnDefinition = "TEXT")
    @Comment("최종 답변")
    private String finalAnswer;

    @Column(name = "is_modified", nullable = false)
    @Builder.Default
    @Comment("수정 여부")
    private Boolean isModified = false;

    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    @Comment("AI 피드백")
    private String aiFeedback;

    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false, updatable = false)
    @Comment("제출시각")
    private LocalDateTime submittedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Comment("수정시각")
    private LocalDateTime updatedAt;
}

