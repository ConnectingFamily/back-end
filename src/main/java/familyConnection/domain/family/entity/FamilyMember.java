package familyConnection.domain.family.entity;

import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import familyConnection.domain.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "family_members", uniqueConstraints = {
    @UniqueConstraint(name = "uk_family_members_family_user", columnNames = { "family_id", "user_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyMember {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id")
  @Comment("멤버 아이디")
  private Long memberId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "family_id", nullable = false)
  @Comment("가족")
  private Family family;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @Comment("사용자")
  private User user;

  @Column(name = "role", length = 20, nullable = false)
  @Builder.Default
  @Comment("역할 (ADMIN, MEMBER)")
  private String role = "MEMBER";

  @Column(name = "nickname_in_family", length = 50)
  @Comment("가족 내 닉네임")
  private String nicknameInFamily;

  @CreationTimestamp
  @Column(name = "joined_at", nullable = false, updatable = false)
  @Comment("가입시각")
  private LocalDateTime joinedAt;

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  @Comment("활성 여부")
  private Boolean isActive = true;
}
