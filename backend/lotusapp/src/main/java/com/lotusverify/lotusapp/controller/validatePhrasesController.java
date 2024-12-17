package com.lotusverify.lotusapp.controller;

import com.lotusverify.lotusapp.service.ValidatePhrasesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/gpt")
public class validatePhrasesController {

    @Autowired
    private ValidatePhrasesService gptService;

    @PostMapping("/validate")
    public Map<String, Object> validateDocument(@RequestBody Map<String, String> request) {
        String document = request.get("document");
        String result = gptService.getChatCompletion(document);

        return Map.of(
                "status", result.contains("Error") ? "failure" : "success",
                "data", result
        );
    }
}
