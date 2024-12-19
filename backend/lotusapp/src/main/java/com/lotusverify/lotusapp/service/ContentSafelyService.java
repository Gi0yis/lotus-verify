package com.lotusverify.lotusapp.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ContentSafelyService {
    private final Dotenv dotenv = Dotenv.load();
    private final String endpoint = dotenv.get("CONTENT_SAFELY_ENDPOINT");
    private final String apiKey = dotenv.get("CONTENT_SAFELY_KEY");
    private final RestTemplate restTemplate = new RestTemplate();

    public String analyzeGroundedness(String text) {
        // Configurar el header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Ocp-Apim-Subscription-Key", apiKey);
        headers.set("Content-Type", "application/json");

        // Crear el cuerpo de la solicitud
        String body = """
        {
            "text": "%s",
            "categories": ["Groundedness"]
        }
        """.formatted(text);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        // Realizar la llamada a la API
        String url = endpoint + "/contentmoderator/analyze";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        return response.getBody();
    }
}
