package com.mawus.core.entity;

import com.mawus.core.entity.enums.StationType;
import jakarta.persistence.*;

@Entity(name = "bot$Station")
@Table(name = "bot_station", indexes = {
        @Index(name = "idx_station_api_code", columnList = "api_code", unique = true)
})
public class Station extends BaseUuidEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "api_code", nullable = false, unique = true)
    private String apiCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id")
    private City city;

    @Column(name = "type")
    private String type;

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

    public StationType getType() {
        return StationType.fromId(type);
    }

    public void setType(StationType type) {
        this.type = type.getId();
    }
}