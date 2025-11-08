package familyConnection.question;

import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import familyConnection.family.Family;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_questions",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_daily_questions_family_date", columnNames = {"family_id", "assigned_date"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_question_id")
    @Comment("일일 질문 아이디")
    private Long dailyQuestionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    @Comment("가족")
    private Family family;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @Comment("질문")
    private Question question;

    @Column(name = "assigned_date", nullable = false)
    @Comment("할당 날짜")
    private LocalDate assignedDate;

    @Column(name = "is_all_answered", nullable = false)
    @Builder.Default
    @Comment("모든 답변 완료 여부")
    private Boolean isAllAnswered = false;

    @Column(name = "published_at")
    @Comment("발행시각")
    private LocalDateTime publishedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("생성시각")
    private LocalDateTime createdAt;
}

