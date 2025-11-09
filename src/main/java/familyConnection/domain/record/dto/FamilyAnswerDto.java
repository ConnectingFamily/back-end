package familyConnection.domain.record.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FamilyAnswerDto {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private String answer;           // finalAnswer
    private LocalDateTime submittedAt;
}