package familyConnection.domain.aianswerhelper;

import familyConnection.global.config.GroqClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AnswerAiHelper {

    private final GroqClient groqClient;

    public AiResult analyze(String questionText, String userAnswer) {
        String system = """
                너는 가족 간 대화를 돕는 한국어 상담 보조 AI야.
                반드시 **한국어로만** 대답해. 일본어, 영어, 이모지 위주의 답변은 사용하지 마.
                
                사용자가 쓴 답변을 더 따뜻하고 구체적으로 다듬어 주고,
                그 답변의 감정 톤을 한 단어로 뽑고,
                개선 포인트를 정확히 3줄로 제안해.

                출력은 아래 JSON 형식을 반드시 그대로 따라.
                {
                  "emotion": "따뜻함",
                  "improved": "사용자의 답변을 한국어로 자연스럽게 다듬은 문장",
                  "feedback": [
                    "첫 번째 피드백 (한국어 한 문장)",
                    "두 번째 피드백 (한국어 한 문장)",
                    "세 번째 피드백 (한국어 한 문장)"
                  ]
                }
                JSON 이외의 문장은 쓰지 마.
                """;
        String user = """
                질문: %s
                사용자의 답변 초안: %s
                """.formatted(questionText, userAnswer);

        String raw = groqClient.chat(system, user);
        if (raw == null || raw.isBlank()) {
            // 모델이 뭔가 이상하게 주면 그냥 원본 되돌려주기
            return new AiResult(
                    "따뜻한",
                    userAnswer,
                    List.of("조금 더 구체적으로 적어보세요.", "상대방 감정을 한 번 더 언급해보세요.", "마무리를 따뜻하게 해보세요.")
            );
        }

        String emotion = extract(raw, "\"emotion\":", ",", true);
        String improved = extract(raw, "\"improved\":", ",", true);
        String feedbackBlock = extract(raw, "\"feedback\":", "]", false);

        List<String> feedback = feedbackBlock == null
                ? List.of()
                : Arrays.stream(
                        feedbackBlock
                                .replace("[", "")
                                .replace("]", "")
                                .replace("\"", "")
                                .split(",")
                )
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        return new AiResult(
                emptyToDefault(emotion, "따뜻한"),
                emptyToDefault(improved, userAnswer),
                feedback.isEmpty()
                        ? List.of("조금 더 구체적으로 적어보세요.", "상대방 감정을 한 번 더 언급해보세요.", "마무리를 따뜻하게 해보세요.")
                        : feedback
        );
    }

    private String emptyToDefault(String s, String def) {
        return (s == null || s.isBlank()) ? def : s;
    }

    private String extract(String src, String startToken, String endToken, boolean stripQuotes) {
        int s = src.indexOf(startToken);
        if (s == -1) return null;
        s += startToken.length();
        int e = src.indexOf(endToken, s);
        if (e == -1) e = src.length();
        String sub = src.substring(s, e).trim();
        if (stripQuotes) {
            sub = sub.replaceFirst("^\\s*\"", "").replaceFirst("\"\\s*$", "");
        }
        return sub;
    }

    public record AiResult(String emotion, String improvedAnswer, List<String> feedback) {}
}
