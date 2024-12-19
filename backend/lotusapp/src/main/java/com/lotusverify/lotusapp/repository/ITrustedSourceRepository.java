package com.lotusverify.lotusapp.repository;

import com.lotusverify.lotusapp.model.TrustedSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ITrustedSourceRepository extends JpaRepository<TrustedSource, Long> {

    @Query("SELECT t FROM TrustedSource t WHERE :url LIKE CONCAT('%', t.url, '%')")
    List<TrustedSource> findMatchingSources(@Param("url") String url);
}