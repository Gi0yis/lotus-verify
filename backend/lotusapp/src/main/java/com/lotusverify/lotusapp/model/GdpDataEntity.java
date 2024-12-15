package com.lotusverify.lotusapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "gdp_data")
public class GdpDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String country;
    private String indicator;
    private String year;
    private Double value;

    public GdpDataEntity() {}

    public GdpDataEntity(String country, String indicator, String year, Double value) {
        this.country = country;
        this.indicator = indicator;
        this.year = year;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
