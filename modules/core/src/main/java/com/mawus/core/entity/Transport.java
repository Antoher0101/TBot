package com.mawus.core.entity;

import jakarta.persistence.*;

@Entity(name = "bot$Transport")
@Table(name = "bot_transport")
public class Transport extends BaseUuidEntity {

    @ManyToOne
    @JoinColumn(name = "transport_type_id", nullable = false)
    private TransportType transportType;

    @Column(name = "title", nullable = false)
    private String title;

    public TransportType getTransportType() {
        return transportType;
    }

    public void setTransportType(TransportType transportType) {
        this.transportType = transportType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String tile) {
        this.title = tile;
    }
}
