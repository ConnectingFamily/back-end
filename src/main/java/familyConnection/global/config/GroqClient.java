package familyConnection.global.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Component
public class GroqClient {

    private final WebClient webClient;
    private final String baseUrl;
    private final String apiKey;
    private final String model;

    public GroqClient(
            WebClient.Builder builder,
            @Value("${groq.base-url}") String baseUrl,
            @Value("${groq.api-key}") String apiKey,
            // ⚠️ 여기 모델 이름이 진짜 Groq에 있는 걸로 들어와야 함
            @Value("${groq.model}") String model
    ) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.webClient = builder.build();
    }

    public String chat(String systemPrompt, String userPrompt) {
        ChatRequest request = new ChatRequest();
        request.setModel(model);
        request.setMessages(List.of(
                new ChatMessage("system", systemPrompt),
                new ChatMessage("user", userPrompt)
        ));

        try {
            return webClient.post()
                    .uri(baseUrl + "/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatResponse.class)
                    .map(response -> response.getChoices().get(0).getMessage().getContent())
                    .block();
        } catch (WebClientResponseException e) {
            // 👇 Groq가 왜 400을 줬는지 여기 찍힘
            System.out.println("Groq status: " + e.getStatusCode());
            System.out.println("Groq body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Groq API 호출 실패", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Groq API 호출 실패", e);
        }
    }

    @Data
    static class ChatRequest {
        private String model;
        private List<ChatMessage> messages;
    }

    @Data
    static class ChatMessage {
        private String role;
        private String content;

        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    @Data
    static class ChatResponse {
        private List<Choice> choices;

        @Data
        static class Choice {
            private ChatMessage message;
        }
    }
}
