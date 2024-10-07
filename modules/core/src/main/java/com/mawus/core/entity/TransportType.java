package com.mawus.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity(name = "bot$TransportType")
@Table(name = "bot_transport_type")
public class TransportType extends BaseUuidEntity {

    @Column(name = "name", nullable = false)
    private String name;

    /**
     * According with Yandex API
     * plane — самолет;
     * train — поезд;
     * suburban — электричка;
     * bus — автобус;
     * water — морской транспорт;
     * helicopter — вертолет.
     */
    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "icon")
    private String icon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
