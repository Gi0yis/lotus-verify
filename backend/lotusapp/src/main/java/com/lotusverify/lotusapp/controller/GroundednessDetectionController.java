package com.lotusverify.lotusapp.controller;

import com.lotusverify.lotusapp.model.GroundednessDetectionRequest;
import com.lotusverify.lotusapp.service.GroundednessDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groundedness")
public class GroundednessDetectionController {

    @Autowired
    private GroundednessDetectionService detectionService;

    @PostMapping("/detect")
    public String detectGroundedness(@RequestBody GroundednessDetectionRequest request) {
        try {
            return detectionService.detectGroundedness(request);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}