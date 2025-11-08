package familyConnection.reminder;

import familyConnection.question.DailyQuestion;
import familyConnection.user.User;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "answer_reminders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reminder_id")
    @Comment("리마인더 아이디")
    private Long reminderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_question_id", nullable = false)
    @Comment("일일 질문")
    private DailyQuestion dailyQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    @Comment("보낸 사용자")
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    @Comment("받은 사용자")
    private User toUser;

    @CreationTimestamp
    @Column(name = "sent_at", nullable = false, updatable = false)
    @Comment("전송시각")
    private LocalDateTime sentAt;
}

