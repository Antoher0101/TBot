package com.mawus.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity(name = "bot$Transport")
@Table(name = "bot_transport")
public class Transport extends BaseUuidEntity {
    @Column(nullable = false)
    private String type; // e.g., car, plane, train

    @Column(nullable = false)
    private String number;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
