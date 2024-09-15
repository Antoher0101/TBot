package com.mawus.core.domain;

import com.mawus.core.entity.Client;
import com.mawus.core.entity.Trip;

import java.io.Serial;
import java.io.Serializable;

public class ClientTrip implements Serializable {

    @Serial
    private static final long serialVersionUID = 7879207295678433640L;

    private Trip trip;

    private Client client;

    public ClientTrip(Trip trip, Client client) {
        this.trip = trip;
        this.client = client;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
