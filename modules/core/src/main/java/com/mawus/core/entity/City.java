package com.mawus.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity(name = "bot$City")
@Table(name = "bot_city", indexes = {
        @Index(name = "IDX_BOT_CITY_ON_TITLE", columnList = "title"),
        @Index(name = "IDX_BOT_CITY_ON_API_CODE", columnList = "apiCode", unique = true)
})
public class City extends BaseUuidEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "api_code", unique = true, nullable = false)
    private String apiCode;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getApiCode() {
        return apiCode;
    }

    public void setApiCode(String apiCode) {
        this.apiCode = apiCode;
    }
}
