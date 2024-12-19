package com.lotusverify.lotusapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lotusverify.lotusapp.model.GroundednessDetectionRequest;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class GroundednessDetectionService {
    private final Dotenv dotenv = Dotenv.load();
    private final String subscriptionKey = dotenv.get("CONTENT_SAFELY_KEY");;
    private final String endpoint = dotenv.get("CONTENT_SAFELY_ENDPOINT");;
    private final OkHttpClient client;


    public GroundednessDetectionService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    public String detectGroundedness(GroundednessDetectionRequest requestBody) throws Exception {
        String apiVersion = "2024-09-15-preview";
        String apiUrl = String.format("%s/contentsafety/text:detectGroundedness?api-version=%s", endpoint, apiVersion);

        ObjectMapper mapper = new ObjectMapper();
        String jsonBody = mapper.writeValueAsString(requestBody);

        Request request = new Request.Builder()
                .url(apiUrl)
                .header("Ocp-Apim-Subscription-Key", subscriptionKey)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                throw new Exception("Request failed with code: " + response.code());
            }
        }
    }
}
