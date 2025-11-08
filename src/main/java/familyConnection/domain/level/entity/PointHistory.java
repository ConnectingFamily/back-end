package familyConnection.domain.level.entity;

import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import familyConnection.domain.family.entity.Family;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "point_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_history_id")
    @Comment("포인트 히스토리 아이디")
    private Long pointHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    @Comment("가족")
    private Family family;

    @Column(name = "point_type", length = 50, nullable = false)
    @Comment("포인트 타입 (ALL_PARTICIPATED, COMMENT)")
    private String pointType;

    @Column(name = "points", nullable = false)
    @Comment("포인트")
    private Integer points;

    @Column(name = "description", length = 255)
    @Comment("설명")
    private String description;

    @CreationTimestamp
    @Column(name = "earned_at", nullable = false, updatable = false)
    @Comment("획득시각")
    private LocalDateTime earnedAt;

    @Column(name = "related_date")
    @Comment("관련 날짜")
    private LocalDate relatedDate;
}

