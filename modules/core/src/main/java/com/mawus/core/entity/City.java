package com.mawus.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity(name = "bot$City")
@Table(name = "bot_city", indexes = @Index(columnList = "name"))
public class City extends BaseUuidEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "api_code", unique = true, nullable = false)
    private String apiCode;

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
}
