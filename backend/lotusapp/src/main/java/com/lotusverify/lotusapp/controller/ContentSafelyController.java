package com.lotusverify.lotusapp.controller;

import com.lotusverify.lotusapp.service.ContentSafelyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContentSafelyController {
    private final ContentSafelyService contentSafelyService;

    public ContentSafelyController(ContentSafelyService contentSafelyService) {
        this.contentSafelyService = contentSafelyService;
    }

    @GetMapping("/analyze-groundedness")
    public String analyzeGroundedness(@RequestParam String text) {
        return contentSafelyService.analyzeGroundedness(text);
    }
}
