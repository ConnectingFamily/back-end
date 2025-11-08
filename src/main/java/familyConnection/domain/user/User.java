package familyConnection.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_kakao_id", columnNames = "kakao_id")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @Comment("유저 아이디")
    private Long id;

    @Column(name = "kakao_id", length = 50, nullable = false)
    @Comment("카카오 계정 식별자")
    private String kakaoId; // ★ 필수 & UNIQUE

    @Column(name = "email", length = 255)
    @Comment("이메일 (없을 수 있음)")
    private String email;

    @Column(name = "nickname", length = 50)
    @Comment("닉네임")
    private String nickname;

    @Column(name = "profile_image_url", length = 500)
    @Comment("프로필 사진 주소")
    private String profileImageUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @Comment("생성시각")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Comment("수정시각")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    @Comment("마지막 접속 시각")
    private LocalDateTime lastLoginAt;

    @Column(name = "is_active", columnDefinition = "TINYINT(1)")
    @Comment("활성 여부")
    private Boolean isActive;
}

