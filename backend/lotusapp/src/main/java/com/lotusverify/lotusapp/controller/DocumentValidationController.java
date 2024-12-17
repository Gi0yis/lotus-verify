package com.lotusverify.lotusapp.controller;

import com.lotusverify.lotusapp.model.DocumentRequest;
import com.lotusverify.lotusapp.model.ValidationReport;
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
    public ResponseEntity<ValidationReport> validateDocument(@RequestBody DocumentRequest request) {
        if (request.getText() == null || request.getText().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            ValidationReport validationReport = documentValidationService.validateDocument(request.getText());
            return ResponseEntity.ok(validationReport);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("El servicio de validación está operativo.");
    }

    @GetMapping("/config")
    public ResponseEntity<String> getConfigDetails() {
        return ResponseEntity.ok("Configuración básica del servicio de validación activa.");
    }
}
