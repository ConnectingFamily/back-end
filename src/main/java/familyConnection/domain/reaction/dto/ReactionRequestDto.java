package familyConnection.domain.reaction.dto;

import lombok.Getter;

@Getter
public class ReactionRequestDto {
    private String targetType;
    private Long targetId;
    private String emojiType;
}