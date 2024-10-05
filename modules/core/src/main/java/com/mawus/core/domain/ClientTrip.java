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

    private int currentPage = 1;

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

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    @Override
    public String toString() {
        return "ClientTrip{" +
                "tripQuery=" + tripQuery +
                ", client=" + client +
                ", currentPage=" + currentPage +
                '}';
    }
}
