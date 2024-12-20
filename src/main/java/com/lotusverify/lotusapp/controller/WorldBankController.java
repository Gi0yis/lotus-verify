package com.lotusverify.lotusapp.controller;

import com.lotusverify.lotusapp.model.GdpData;
import com.lotusverify.lotusapp.service.GdpDatabaseService;
import com.lotusverify.lotusapp.service.WorldBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/worldbank")
public class WorldBankController {

    @Autowired
    private WorldBankService worldBankService;

    @Autowired
    private GdpDatabaseService databaseService;

    @GetMapping("/gdp")
    public ResponseEntity<List<GdpData>> getGpaAndSaveData(
            @RequestParam String countryCode,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        List<GdpData> gdpData = worldBankService.getParsedGdpData(countryCode, startDate, endDate);
        databaseService.saveGdpData(gdpData);

        return ResponseEntity.ok(gdpData);
    }
}
