package familyConnection.domain.level.policy;

import org.springframework.stereotype.Component;

@Component
public class AnswerCountLevelPolicy {

    public int resolveLevelByAnswerCount(int answerCount) {
        if (answerCount >= 150) return 6;     // 이심전심
        if (answerCount >= 100) return 5;     // 따뜻한 마음
        if (answerCount >= 60)  return 4;     // 닮는 마음
        if (answerCount >= 30)  return 3;     // 열린 마음
        if (answerCount >= 10)  return 2;     // 스치는 마음
        return 1;                             // 낯선 마음
    }

    public String resolveLevelName(int level) {
        return switch (level) {
            case 1 -> "낯선 마음";
            case 2 -> "스치는 마음";
            case 3 -> "열린 마음";
            case 4 -> "닮는 마음";
            case 5 -> "따뜻한 마음";
            case 6 -> "이심전심";
            default -> "낯선 마음";
        };
    }
}
