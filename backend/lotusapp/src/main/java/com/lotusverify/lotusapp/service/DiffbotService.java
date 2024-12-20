package com.lotusverify.lotusapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DiffbotService {

    private KeyVaultService keyVaultService;

    public DiffbotService(KeyVaultService keyVaultService) {
        this.keyVaultService = keyVaultService;
    }

    @Value("${diffbot.max.text.length:1000}")
    private int maxTextLength;

    @Autowired
    private RestTemplate restTemplate;

    public String extractTextFromUrl(String url) throws JsonProcessingException {
        var apiUrl = String.format(
                "https://api.diffbot.com/v3/article?token=%s&url=%s",
                keyVaultService.getSecret("DIFFBOT-TOKEN"),
                url
        );

        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode firstObjectNode = getJsonNode(response);

            var fullText = firstObjectNode.path("text").asText();

            return fullText.length() > maxTextLength
                    ? fullText.substring(0, maxTextLength)
                    : fullText;
        } else {
            throw new RuntimeException("Error al extraer el texto: " + response.getStatusCode());
        }
    }

    @NotNull
    private static JsonNode getJsonNode(ResponseEntity<String> response) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());

        if (!rootNode.has("objects")) {
            throw new RuntimeException("La respuesta no contiene el nodo 'objects'.");
        }

        JsonNode objectsNode = rootNode.path("objects");
        if (!objectsNode.isArray() || objectsNode.isEmpty()) {
            throw new RuntimeException("El nodo 'objects' está vacío o no es un arreglo.");
        }

        JsonNode firstObjectNode = objectsNode.get(0);
        if (firstObjectNode == null || !firstObjectNode.has("text")) {
            throw new RuntimeException("El nodo 'text' no está presente en el primer objeto de 'objects'.");
        }
        return firstObjectNode;
    }

}
