package familyConnection.notification;

import familyConnection.user.User;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fcm_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    @Comment("토큰 아이디")
    private Long tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("사용자")
    private User user;

    @Column(name = "fcm_token", length = 500, nullable = false)
    @Comment("FCM 토큰")
    private String fcmToken;

    @Column(name = "device_type", length = 20)
    @Comment("디바이스 타입 (IOS, ANDROID)")
    private String deviceType;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    @Comment("활성 여부")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("생성시각")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Comment("수정시각")
    private LocalDateTime updatedAt;
}

