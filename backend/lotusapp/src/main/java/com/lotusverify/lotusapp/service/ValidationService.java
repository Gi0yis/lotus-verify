package com.lotusverify.lotusapp.service;

import com.lotusverify.lotusapp.model.GdpDataEntity;
import com.lotusverify.lotusapp.repository.IGdpDataRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ValidationService {

    private final IGdpDataRepository gdpDataRepository;

    public ValidationService(IGdpDataRepository gdpDataRepository) {
        this.gdpDataRepository = gdpDataRepository;
    }

    public String validateStatement(String statement) {
        var country = extractCountry(statement);
        var year = extractYear(statement);
        var claimedValue = extractClaimedValue(statement);

        if (country == null || year == null || claimedValue == null)
            return "Error: No se pudo analizar afirmación. Verifique el formato.";

        Optional<GdpDataEntity> record = gdpDataRepository.findByCountryAndYear(country, year);

        if (record.isPresent()) {
            var actualValue = record.get().getValue();
            var tolerance = 0.05 * actualValue;

            if (Math.abs(actualValue - claimedValue) <= tolerance)
                return "Afirmación válida: El valor reclamado está dentro del rango aceptable";
            else
                return "Afirmación incorrecta: El valor real es " + actualValue + ".";
        }
        else
            return "Datos no encontrados para " + country + " en el año " + year + ".";
    }

    private String extractCountry(String statement) {
        Pattern pattern = Pattern.compile("de ([A-Z][a-z]+(?:\\s[A-Z][a-z]+)?)");
        Matcher matcher = pattern.matcher(statement);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String extractYear(String statement) {
        var pattern = Pattern.compile("\\b(\\d{4}\\b)");
        Matcher matcher = pattern.matcher(statement);
        return matcher.find() ? matcher.group(1) : null;
    }

    private Double extractClaimedValue(String statement) {
        Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)?)\\s*(trillón(?:es)?|millón(?:es)?|billón(?:es)?)");
        Matcher matcher = pattern.matcher(statement);

        if (matcher.find()) {
            var value = Double.parseDouble(matcher.group(1));
            var scale = matcher.group(3);

            switch (scale) {
                case "trillónes" -> {
                    return value + 1E12;
                }
                case "billónes" -> {
                    return value + 1E9;
                }
                case "millónes" -> {
                    return value + 1E6;
                }
                default -> {
                    return value;
                }
            }
        }
        return null;
    }
}
