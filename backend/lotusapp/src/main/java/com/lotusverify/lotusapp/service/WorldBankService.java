package com.lotusverify.lotusapp.service;

import com.lotusverify.lotusapp.model.GdpData;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WorldBankService {
    private final Dotenv dotenv = Dotenv.load();
    private final String BASE_URL = dotenv.get("WORLD_BANK_URL");

    private final RestTemplate restTemplate;

    public WorldBankService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<GdpData> getParsedGdpData(String countryCode, String startDate, String endDate) {
        String url = String.format("%s/country/%s/indicator/NY.GDP.MKTP.CD?format=json&date=%s:%s&per_page=100",
                BASE_URL,
                countryCode,
                startDate,
                endDate);

        ResponseEntity<Object[]> response = restTemplate.getForEntity(url, Object[].class);

        List<Map<String, Object>> records = null;

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Object[] data = response.getBody();
            records = (List<Map<String, Object>>) data[1];
            List<GdpData> result = new ArrayList<>();

            for (Map<String, Object> record : records) {
                Map<String, Object> country = (Map<String, Object>) record.get("country");
                Map<String, Object> indicator = (Map<String, Object>) record.get("indicator");

                result.add(new GdpData(
                        (String) country.get("value"),
                        (String) indicator.get("value"),
                        (String) record.get("date"),
                        record.get("value") != null ? Double.parseDouble(record.get("value").toString()) : null
                ));
            }

            return result;
        } else {
            throw new RuntimeException("Error al obtener datos de la API del Banco Mundial");
        }
    }
}
