package com.mawus.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity(name = "bot$TransportType")
@Table(name = "bot_transport_type")
public class TransportType extends BaseUuidEntity {

    @Column(name = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
