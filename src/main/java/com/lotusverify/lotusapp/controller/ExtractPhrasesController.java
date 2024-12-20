package com.lotusverify.lotusapp.controller;

import com.lotusverify.lotusapp.service.ExtractPhrasesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gpt")
public class ExtractPhrasesController {

    @Autowired
    private ExtractPhrasesService extractPhrasesService;

    public static class TextRequest {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    @PostMapping("/extract-phrases")
    public ResponseEntity<List<String>> extractRelevantPhrases(@RequestBody TextRequest request) {
        if (request.getText() == null || request.getText().isEmpty()) {
            return ResponseEntity.badRequest().body(List.of("Error: El texto proporcionado está vacío."));
        }

        List<String> relevantPhrases = extractPhrasesService.extractRelevantPhrases(request.getText());

        List<String> cleanPhrases = relevantPhrases.stream()
                .flatMap(response -> Arrays.stream(response.split("###")))
                .map(String::trim)
                .filter(phrase -> !phrase.isEmpty())
                .collect(Collectors.toList());

        return ResponseEntity.ok(cleanPhrases);
    }
}
