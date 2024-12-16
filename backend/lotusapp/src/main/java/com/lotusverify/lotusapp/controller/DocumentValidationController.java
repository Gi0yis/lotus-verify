package com.lotusverify.lotusapp.controller;

import com.lotusverify.lotusapp.model.DocumentRequest;
import com.lotusverify.lotusapp.service.DocumentValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/validate")
public class DocumentValidationController {

    @Autowired
    private DocumentValidationService documentValidationService;

    @PostMapping
    public ResponseEntity<String> validateDocument(@RequestBody DocumentRequest request) {
        if (request.getText() == null || request.getText().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El texto no puede estar vacío.");
        }

        try {
            var validationReport = documentValidationService.validateDocument(request.getText());
            return ResponseEntity.ok(validationReport);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    "Ocurrió un error durante la validación del documento: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("El servicio de validación está operativo.");
    }
}
