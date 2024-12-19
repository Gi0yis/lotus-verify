package com.lotusverify.lotusapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lotusverify.lotusapp.model.SearchResult;
import com.lotusverify.lotusapp.model.TrustedSource;
import com.lotusverify.lotusapp.repository.ISearchResultRepository;
import com.lotusverify.lotusapp.repository.ITrustedSourceRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BingSearchService {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String SUBSCRIPTION_KEY = dotenv.get("BING_API_KEY");
    private static final String ENDPOINT = dotenv.get("BING_API_URL");

    @Autowired
    private ISearchResultRepository searchResultRepository;

    @Autowired
    private ITrustedSourceRepository trustedSourceRepository;

    public String search(String query) {
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.set("Ocp-Apim-Subscription-Key", SUBSCRIPTION_KEY);

        var url = ENDPOINT + "?q=" + query;
        var entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            var responseBody = response.getBody();

            var mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody), webPages = root.path("webPages").path("value");

            if (webPages.isMissingNode() || !webPages.isArray() || webPages.isEmpty()) {
                return "No se encontraron resultados para la consulta: " + query;
            }

            List<JsonNode> trustedResults = new ArrayList<>();
            List<JsonNode> normalResults = new ArrayList<>();

            for (JsonNode page : webPages) {
                String urlResult = page.path("url").asText();
                List<TrustedSource> matchingSources = trustedSourceRepository.findMatchingSources(urlResult);

                if (!matchingSources.isEmpty()) {
                    trustedResults.add(page);
                } else {
                    normalResults.add(page);
                }
            }

            var result = new StringBuilder();
            int count = 0;

            // Priorizar resultados confiables
            for (JsonNode page : trustedResults) {
                if (count >= 2) break;
                appendResult(page, result);
                count++;
            }

            // Si faltan resultados, completar con resultados normales
            if (count < 2) {
                for (JsonNode page : normalResults) {
                    if (count >= 2) break;
                    appendResult(page, result);
                    count++;
                }
            }

            var searchResult = new SearchResult(query, result.toString(), LocalDateTime.now());
            searchResultRepository.save(searchResult);

            return result.toString();
        } catch (HttpClientErrorException e) {
            return "Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
        } catch (Exception e) {
            return "Error al procesar la respuesta: " + e.getMessage();
        }
    }

    private void appendResult(JsonNode page, StringBuilder result) {
        var name = page.path("name").asText();
        var urlResult = page.path("url").asText();
        var snippet = page.path("snippet").asText();

        result.append("Titulo: ").append(name).append("\n")
                .append("URL: ").append(urlResult).append("\n")
                .append("Descripción: ").append(snippet).append("\n\n");
    }

    public int calculateRelevancy(String query, String searchResults) {
        int score = 0;
        String[] keywords = query.split(" ");
        for (String keyword : keywords) {
            score += searchResults.toLowerCase().contains(keyword.toLowerCase()) ? 10 : 0;
        }
        return score;
    }
}
