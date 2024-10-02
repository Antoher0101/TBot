package com.mawus.core.entity;

import jakarta.persistence.*;

@Entity(name = "bot$Station")
@Table(name = "bot_station", indexes = {
        @Index(name = "idx_station_api_code", columnList = "api_code")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_station_api_code", columnNames = {"api_code"})
})
public class Station extends BaseUuidEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "api_code", unique = true, nullable = false)
    private String apiCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    //todo Type

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiCode() {
        return apiCode;
    }

    public void setApiCode(String apiCode) {
        this.apiCode = apiCode;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}