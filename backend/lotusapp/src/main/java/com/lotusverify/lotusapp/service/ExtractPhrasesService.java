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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ExtractPhrasesService {
    private final Dotenv dotenv = Dotenv.load();
    private final String ENDPOINT1 = dotenv.get("EXTRACT_MODEL_ENDPOINT1");
    private final String API_KEY1 = dotenv.get("EXTRACT_MODEL_KEY1");
    private final String ENDPOINT3 = dotenv.get("EXTRACT_MODEL_ENDPOINT3");
    private final String API_KEY3 = dotenv.get("EXTRACT_MODEL_KEY3");

    private final int MAX_TOKENS_PER_REQUEST = 1000;
    private final AtomicInteger modelCounter = new AtomicInteger(0);

    public List<String> extractRelevantPhrases(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.singletonList("Error: El texto proporcionado está vacío.");
        }

        List<String> textChunks = splitTextIntoChunks(text, MAX_TOKENS_PER_REQUEST);
        List<String> relevantPhrases = new ArrayList<>();

        for (String chunk : textChunks) {
            List<String> responsePhrases = invokeModel(chunk);
            relevantPhrases.addAll(responsePhrases);
        }

        return relevantPhrases;
    }

    private List<String> invokeModel(String textChunk) {
        int modelIndex = modelCounter.getAndIncrement() % 2;
        String endpoint = modelIndex == 0 ? ENDPOINT1 : ENDPOINT3;
        String apiKey = modelIndex == 0 ? API_KEY1 : API_KEY3;

        return callModelApi(textChunk, endpoint, apiKey);
    }

    private List<String> callModelApi(String textChunk, String endpoint, String apiKey) {
        var entity = createHttpEntity(textChunk, apiKey);

        try {
            var restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, String.class);

            var objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());

            String rawResponse = root.path("choices").get(0).path("message").path("content").asText();
            return cleanResponseText(rawResponse);
        } catch (Exception e) {
            return Collections.singletonList("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    @NotNull
    private HttpEntity<Map<String, Object>> createHttpEntity(String prompt, String apiKey) {
        var headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("api-key", apiKey);

        Map<String, Object> requestBody = Map.of(
                "messages", List.of(
                        Map.of("role", "system", "content", """
                            Procesa el siguiente texto en tres etapas:

                            Extrae entidades clave: Identifica los conceptos principales, como temas, términos, tecnologías, etc., mencionados en el texto. Estas entidades deben ser palabras o frases cortas que representen el tema del contenido.
                            Identifica frases relevantes: Selecciona las frases más importantes que contengan información clave del texto, asegurando que estén relacionadas con las entidades extraídas.
                            Reformula las frases relevantes con contexto: Reescribe las frases relevantes para incluir explícitamente las entidades extraídas, proporcionando un contexto claro y directo.
                            Ejemplo de entrada: "Convergencia de la Inteligencia Artificial, el Aprendizaje Automático y el Internet de las Cosas está dando lugar a una nueva era de innovación y transformación digital. Esta transformación está cambiando la forma en que vivimos, trabajamos y nos comunicamos."

                            Resultados esperados:

                            Entidades extraídas: [Inteligencia Artificial, Aprendizaje Automático, Internet de las Cosas]
                            Frases relevantes: [La convergencia de tecnologías está dando lugar a una nueva era de innovación., Esta transformación digital está cambiando nuestras formas de vida, trabajo y comunicación.]
                            Frases contextualizadas: [La convergencia de la Inteligencia Artificial, el Aprendizaje Automático y el Internet de las Cosas está dando lugar a una nueva era de innovación., La Inteligencia Artificial, el Aprendizaje Automático y el Internet de las Cosas están transformando nuestras formas de vida, trabajo y comunicación.]
                            Texto a procesar: "{input_text}"
                                
                            Solo devuelve el resultado de las Frases contextualizadas en el siguiente formato solo eso no envies mas cosas(SOLO QUIERO LA LISTA DE FRASES CONTEXTUALIZADAS):
                            Lista de frases reformuladas con contexto
                            
                            Las frases deben ser cortas ya que quiero hacer busquedas en internet con ellas.
                            Asegúrate de devolver únicamente las frases, sin listas, sin corchetes y sin ningún otro formato adicional. Separa cada frase con el delimitador ###."""), Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", 200,
                "temperature", 0.3
        );

        return new HttpEntity<>(requestBody, headers);
    }

    /*
     Divide el texto en fragmentos basados en límites de tokens.
     */
    private List<String> splitTextIntoChunks(String text, int maxTokens) {
        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + maxTokens, text.length());
            chunks.add(text.substring(start, end));
            start = end;
        }

        return chunks;
    }

    private List<String> cleanResponseText(String rawResponse) {
        if (rawResponse == null || rawResponse.isEmpty()) {
            return Collections.emptyList();
        }

        String[] sentences = rawResponse.split("###");

        List<String> cleanSentences = new ArrayList<>();
        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (!trimmed.isEmpty()) {
                cleanSentences.add(trimmed);
            }
        }

        return cleanSentences;
    }

}
