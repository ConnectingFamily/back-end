package familyConnection.domain.reaction.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReactionResponseDto {
    private Long reactionId;
    private String targetType;
    private Long targetId;
    private Long userId;
    private String emojiType;
    private LocalDateTime createdAt;
}
