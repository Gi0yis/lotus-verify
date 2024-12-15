package com.lotusverify.lotusapp.controller;

import com.lotusverify.lotusapp.service.ValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidationController {
    private final ValidationService validationService;

    public ValidationController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateStatement(@RequestParam String statement) {
        var result = validationService.validateStatement(statement);
        return ResponseEntity.ok(result);
    }
}
