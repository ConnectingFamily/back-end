package familyConnection.domain.notification.entity;

import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import familyConnection.domain.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    @Comment("알림 아이디")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("사용자")
    private User user;

    @Column(name = "notification_type", length = 50, nullable = false)
    @Comment("알림 타입 (ANSWER_PUBLISHED, COMMENT_ADDED, REMIND_ANSWER, LEVEL_UP)")
    private String notificationType;

    @Column(name = "title", length = 255, nullable = false)
    @Comment("제목")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    @Comment("내용")
    private String content;

    @Column(name = "related_type", length = 50)
    @Comment("관련 타입 (DAILY_QUESTION, COMMENT, FAMILY_LEVEL)")
    private String relatedType;

    @Column(name = "related_id")
    @Comment("관련 아이디")
    private Long relatedId;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    @Comment("읽음 여부")
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("생성시각")
    private LocalDateTime createdAt;
}

