package com.lotusverify.lotusapp.controller;

import com.lotusverify.lotusapp.service.BingSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BingSearchController {
    @Autowired
    private BingSearchService bingSearchService;

    @GetMapping("/search")
    public String search(@RequestParam String query) {
        return bingSearchService.search(query);
    }

//    public ResponseEntity<Map<String, Object>> search(@RequestParam String query) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("query", query);
//        response.put("results", bingSearchService.search(query));
//        return ResponseEntity.ok(response);
//    }
}
