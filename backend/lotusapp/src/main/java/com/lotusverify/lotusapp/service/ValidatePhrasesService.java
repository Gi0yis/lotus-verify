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
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ValidatePhrasesService {
    private final Dotenv dotenv = Dotenv.load();
    private final String ENDPOINT1 = dotenv.get("VALIDATE_MODEL_ENDPOINT1");
    private final String ENDPOINT2 = dotenv.get("VALIDATE_MODEL_ENDPOINT2");
    private final String ENDPOINT3 = dotenv.get("VALIDATE_MODEL_ENDPOINT3");
    private final String ENDPOINT4 = dotenv.get("VALIDATE_MODEL_ENDPOINT4");
    private final String API_KEY = dotenv.get("VALIDATE_MODEL_KEY");

    private final AtomicInteger requestCounter = new AtomicInteger(0);

    public String getChatCompletion(String prompt) {
        int requestIndex = requestCounter.getAndIncrement() % 4;
        switch (requestIndex) {
            case 0 -> {
                return invokeModel(prompt, ENDPOINT1, API_KEY);
            }
            case 1 -> {
                return invokeModel(prompt, ENDPOINT2, API_KEY);
            }
            case 2 -> {
                return invokeModel(prompt, ENDPOINT3, API_KEY);
            }
            case 3 -> {
                return invokeModel(prompt, ENDPOINT4, API_KEY);
            }
            default -> throw new IllegalStateException("Índice de modelo inesperado");
        }
    }

    private String invokeModel(String prompt, String endpoint, String apiKey) {
        if (prompt == null || prompt.trim().isEmpty()) {
            return "Error: El contenido proporcionado está vacío.";
        }

        var entity = getMapHttpEntity(prompt, apiKey);

        try {
            var restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, String.class);

            var objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());

            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            return "Error: No se pudo completar la solicitud. " + e.getMessage();
        }
    }

    @NotNull
    private HttpEntity<Map<String, Object>> getMapHttpEntity(String prompt, String apiKey) {
        var headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("api-key", apiKey);

        Map<String, Object> requestBody = Map.of(
                "messages", List.of(
                        Map.of("role", "system", "content", """
                                Soy LotusVerify, un asistente especializado en la verificación de datos.
                                Mi tarea es verificar la exactitud de la información que proporcionas,
                                devolviendo solo las palabra 'true' en minúsculas, según corresponda.
                                Si no es aplicable, devolveré 'false'. No realizaré búsquedas en internet;
                                me baso únicamente en el conocimiento previo para la verificación."""),
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", 500,
                "temperature", 0.5
        );

        return new HttpEntity<>(requestBody, headers);
    }
}