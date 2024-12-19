package com.lotusverify.lotusapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DiffbotService {

    @Value("${diffbot.api.token}")
    private String apiToken;

    @Autowired
    private RestTemplate restTemplate;

    public String extractTextFromUrl(String url) throws JsonProcessingException {
        String apiUrl = String.format(
                "https://api.diffbot.com/v3/article?token=%s&url=%s",
                apiToken,
                url
        );

        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // Procesar la respuesta JSON para extraer el texto
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode textNode = rootNode.path("objects").get(0).path("text");
            return textNode.asText();
        } else {
            throw new RuntimeException("Error al extraer el texto: " + response.getStatusCode());
        }
    }
}