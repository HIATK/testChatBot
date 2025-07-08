package org.example.testchat.controller;

import org.example.testchat.service.OllamaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Controller
public class ChatController {

    @Autowired
    private OllamaService ollamaService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/test")
    @ResponseBody
    public Mono<String> testOllama() {
        return ollamaService.generateResponse("Hello, respond with 'Connection successful!'");
    }

    @GetMapping("/debug")
    @ResponseBody
    public Mono<String> debugOllama() {
        // 올라마 서버 상태 확인
        return WebClient.create()
                .get()
                .uri("http://localhost:11434/api/tags")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> "Ollama 서버 연결 성공! 사용 가능한 모델: " + response)
                .onErrorReturn("Ollama 서버에 연결할 수 없습니다.");
    }

    @PostMapping("/chat")
    @ResponseBody
    public Mono<String> chat(@RequestBody ChatRequest request) {
        return ollamaService.generateResponse(request.getMessage());
    }

    public static class ChatRequest {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}