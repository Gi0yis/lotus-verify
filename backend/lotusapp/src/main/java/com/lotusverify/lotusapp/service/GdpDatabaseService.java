package com.lotusverify.lotusapp.service;

import com.lotusverify.lotusapp.model.GdpData;
import com.lotusverify.lotusapp.model.GdpDataEntity;
import com.lotusverify.lotusapp.repository.IGdpDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GdpDatabaseService {
    private final IGdpDataRepository repository;

    public GdpDatabaseService(IGdpDataRepository repository) {
        this.repository = repository;
    }

    public void saveGdpData(List<GdpData> gdpDataList) {
        List<GdpDataEntity> entities = gdpDataList.stream().map(data ->
                new GdpDataEntity(data.getCountry(), data.getIndicator(), data.getYear(), data.getValue()))
                .collect(Collectors.toList());

        repository.saveAll(entities);
    }
}
