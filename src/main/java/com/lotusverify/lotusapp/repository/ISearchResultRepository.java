package com.lotusverify.lotusapp.repository;

import com.lotusverify.lotusapp.model.SearchResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISearchResultRepository extends JpaRepository<SearchResult, Long> {}
