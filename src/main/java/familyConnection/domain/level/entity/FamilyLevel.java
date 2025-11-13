package familyConnection.domain.level.entity;

import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.UpdateTimestamp;

import familyConnection.domain.family.entity.Family;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "family_levels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "level_id")
    @Comment("레벨 아이디")
    private Long levelId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false, unique = true)
    @Comment("가족")
    private Family family;

    @Column(name = "current_level", nullable = false)
    @Builder.Default
    @Comment("현재 레벨")
    private Integer currentLevel = 1;

    @Column(name = "current_points", nullable = false)
    @Builder.Default
    @Comment("현재 포인트")
    private Integer currentPoints = 0;

    @Column(name = "level_name", length = 50)
    @Comment("레벨 이름")
    private String levelName;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Comment("수정시각")
    private LocalDateTime updatedAt;

    public void decreaseAnswerCount() {
        if (this.currentPoints > 0) {
            this.currentPoints -= 1;
        }
    }
}

