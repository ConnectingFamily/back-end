package familyConnection.domain.reaction.entity;

import familyConnection.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reactions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reactions_target_user_emoji",
                        columnNames = {"target_type", "target_id", "user_id", "emoji_type"}
                )
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reaction_id")
    @Comment("반응 아이디")
    private Long reactionId;


    @Column(name = "target_type", length = 20, nullable = false)
    @Comment("대상 타입 (ANSWER, COMMENT)")
    private String targetType;

    @Column(name = "target_id", nullable = false)
    @Comment("대상 아이디")
    private Long targetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("사용자")
    private User user;

    @Column(name = "emoji_type", length = 50, nullable = false)
    @Comment("이모지 타입")
    private String emojiType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("생성시각")
    private LocalDateTime createdAt;
}
