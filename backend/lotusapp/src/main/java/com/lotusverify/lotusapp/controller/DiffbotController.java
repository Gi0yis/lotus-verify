package com.lotusverify.lotusapp.controller;

import com.lotusverify.lotusapp.service.DiffbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DiffbotController {

    @Autowired
    private DiffbotService diffbotService;

    @GetMapping("/extract")
    public ResponseEntity<String> extractText(@RequestParam String url) {
        try {
            String text = diffbotService.extractTextFromUrl(url);
            return ResponseEntity.ok(text);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al extraer el texto: " + e.getMessage());
        }
    }
}