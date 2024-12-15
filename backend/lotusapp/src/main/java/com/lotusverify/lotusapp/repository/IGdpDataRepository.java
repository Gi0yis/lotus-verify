package com.lotusverify.lotusapp.repository;

import com.lotusverify.lotusapp.model.GdpDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface IGdpDataRepository extends  JpaRepository<GdpDataEntity, Long>{
    Optional<GdpDataEntity> findByCountryAndYear(String country, String year);
}
