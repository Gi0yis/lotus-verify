package com.lotusverify.lotusapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class Gpt4oMiniService {
    private final Dotenv dotenv = Dotenv.load();
    private final String ENDPOINT = dotenv.get("OPENAI_ENDPOINT");
    private final String API_KEY = dotenv.get("OPENAI_API_KEY");

    public String getChatCompletion(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            return "Error: El contenido proporcionado está vacío.";
        }

        var entity = getMapHttpEntity(prompt);

        try {
            var restTemplate = new RestTemplate();
            assert ENDPOINT != null;
            ResponseEntity<String> response = restTemplate.exchange(ENDPOINT, HttpMethod.POST, entity, String.class);

            var objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());

            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            return "Error: No se pudo completar la solicitud. " + e.getMessage();
        }
    }

    @NotNull
    private HttpEntity<Map<String, Object>> getMapHttpEntity(String prompt) {
        var headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("api-key", API_KEY);

        Map<String, Object> requestBody = Map.of(
                "messages", List.of(
                        Map.of("role", "system", "content", "You are LotusVerify, an assistant specialized in factual validation."),
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", 100,
                "temperature", 0.5
        );

        return new HttpEntity<Map<String, Object>>(requestBody, headers);
    }
}
