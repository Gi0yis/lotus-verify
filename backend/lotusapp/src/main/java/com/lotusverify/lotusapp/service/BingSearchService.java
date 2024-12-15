package com.lotusverify.lotusapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lotusverify.lotusapp.model.SearchResult;
import com.lotusverify.lotusapp.repository.ISearchResultRepository;
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

@Service
public class BingSearchService {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String SUBSCRIPTION_KEY = dotenv.get("BING_API_KEY");
    private static final String ENDPOINT = dotenv.get("BING_API_URL");

    @Autowired
    private ISearchResultRepository searchResultRepository;

    public String search(String query) {

        var restTemplate = new RestTemplate();
        /*
        Configuracion de los headers con la clave del api, se construye una url de consulta,
        se realiza una solicitud GET y se devuelve el cuerpo de respuesta.
         */

        var headers = new HttpHeaders();
        headers.set("Ocp-Apim-Subscription-Key", SUBSCRIPTION_KEY);

        var url = ENDPOINT + "?q=" + query;

        var entity = new HttpEntity<String>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            var responseBody = response.getBody();

            /*
            Parsea el Json para Extraer las datos mas relevantes
             */

            var mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody), webPages = root.path("webPages").path("value");

            var result = new StringBuilder();

            for (JsonNode page : webPages) {
                var name = page.path("name").asText();
                var urlResult = page.path("url").asText();
                var snippet = page.path("snippet").asText();

                result.append("Titulo: ").append(name).append("\n")
                        .append("URL: ").append(urlResult).append("\n")
                        .append("Descripción: ").append(snippet).append("\n\n");
            }

            if (root.path("webPages").isMissingNode())
                return "No se encontraron resultados para la consulta: " + query;

            SearchResult searchResult = new SearchResult(query, result.toString(), LocalDateTime.now());
            searchResultRepository.save(searchResult);

            return result.toString();

        } catch (HttpClientErrorException e) {
            return "Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
        } catch (Exception e) {
            return "Error al procesar la respuesta: " + e.getMessage();
        }
    }

}
