package org.example.testchat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class OllamaService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${ollama.url:http://localhost:11434}")
    private String ollamaUrl;

    @Value("${ollama.model:llama3.1:8b}") //api/debbug에 나온 버전 입력 틀리면 작동 안됨
    private String model;

    public OllamaService() {
        this.webClient = WebClient.builder()
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public Mono<String> generateResponse(String prompt) {
        Map<String, Object> request = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", false
        );

        System.out.println("사용할 모델: " + model);
        System.out.println("Ollama 요청: " + request);
        System.out.println("URL: " + ollamaUrl + "/api/generate");

        return webClient.post()
                .uri(ollamaUrl + "/api/generate")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("User-Agent", "Spring-WebClient")
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> {
                            System.err.println("HTTP 오류 상태: " + response.statusCode());
                            return response.bodyToMono(String.class)
                                    .doOnNext(body -> System.err.println("오류 응답 본문: " + body))
                                    .then(Mono.error(new RuntimeException("HTTP 오류: " + response.statusCode())));
                        })
                .bodyToMono(String.class)
                .map(this::extractResponse)
                .doOnNext(response -> System.out.println("성공 응답: " + response))
                .doOnError(error -> {
                    System.err.println("Ollama API 호출 오류: " + error.getMessage());
                    error.printStackTrace();
                })
                .onErrorReturn("죄송합니다. Ollama 서버와 통신 중 오류가 발생했습니다.");
    }

    private String extractResponse(String jsonResponse) {
        try {
            System.out.println("Ollama 응답: " + jsonResponse); // 디버깅용
            JsonNode node = objectMapper.readTree(jsonResponse);

            if (node.has("response")) {
                return node.get("response").asText();
            } else if (node.has("error")) {
                return "오류: " + node.get("error").asText();
            } else {
                return "예상치 못한 응답 형식입니다.";
            }
        } catch (Exception e) {
            System.err.println("JSON 파싱 오류: " + e.getMessage());
            return "응답을 처리하는 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}