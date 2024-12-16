package com.lotusverify.lotusapp.controller;

import com.lotusverify.lotusapp.service.TextAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TextAnalyticsController {
    @Autowired
    private TextAnalyticsService textAnalyticsService;

    @GetMapping("/extract-entities")
    public List<Map<String, String>> extractEntities(@RequestParam String text) {
        return textAnalyticsService.extractEntities(text);
    }
}
