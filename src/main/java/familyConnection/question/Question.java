package familyConnection.question;

import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    @Comment("질문 아이디")
    private Long questionId;

    @Column(name = "question_number", nullable = false)
    @Comment("질문 번호")
    private Integer questionNumber;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    @Comment("질문 내용")
    private String questionText;

    @Column(name = "counseling_technique", length = 50)
    @Comment("상담 기법")
    private String counselingTechnique;

    @Column(name = "description", columnDefinition = "TEXT")
    @Comment("설명")
    private String description;

    @Column(name = "example_answer", columnDefinition = "TEXT")
    @Comment("예시 답변")
    private String exampleAnswer;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    @Comment("활성 여부")
    private Boolean isActive = true;

    @Column(name = "display_order", nullable = false)
    @Comment("표시 순서")
    private Integer displayOrder;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("생성시각")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @Comment("수정시각")
    private LocalDateTime updatedAt;
}

