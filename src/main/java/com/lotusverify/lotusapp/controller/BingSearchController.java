package com.lotusverify.lotusapp.controller;

import com.lotusverify.lotusapp.service.BingSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BingSearchController {

    @Autowired
    private BingSearchService bingSearchService;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(@RequestParam String query) {
        try {
            String results = bingSearchService.search(query);
            Map<String, Object> response = new HashMap<>();
            response.put("query", query);
            response.put("results", results);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Ocurrió un error al realizar la búsqueda: " + e.getMessage()));
        }
    }
}
