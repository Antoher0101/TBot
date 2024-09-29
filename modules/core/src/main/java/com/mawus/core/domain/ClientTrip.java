package com.mawus.core.domain;

import com.mawus.core.entity.Client;
import com.mawus.core.entity.Trip;

import java.io.Serial;
import java.io.Serializable;

public class ClientTrip implements Serializable {

    @Serial
    private static final long serialVersionUID = 7879207295678433640L;

    private TripQuery tripQuery;

    private Client client;

    private int offset = 0;

    public ClientTrip(TripQuery trip, Client client) {
        this.tripQuery = trip;
        this.client = client;
    }

    public TripQuery getTripQuery() {
        return tripQuery;
    }

    public void setTripQuery(TripQuery tripQuery) {
        this.tripQuery = tripQuery;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "ClientTrip{" +
                "trip=" + tripQuery +
                ", client=" + client +
                ", offset=" + offset +
                '}';
    }
}
