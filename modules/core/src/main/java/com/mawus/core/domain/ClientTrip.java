package com.mawus.core.domain;

import com.mawus.core.entity.Client;
import com.mawus.core.entity.Trip;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClientTrip implements Serializable {

    @Serial
    private static final long serialVersionUID = 7879207295678433640L;

    private TripQuery tripQuery;

    private Client client;

    private Trip trip;
    private int currentPage = 1;
    private Long offset = 0L;

    /**
     * Хранит список доступных рейсов, полученных с API, с учетом текущей страницы.
     * Этот список обновляется каждый раз при переходе на новую страницу.
     */
    private List<Trip> availableTrips = new ArrayList<>();

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

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public List<Trip> getAvailableTrips() {
        return availableTrips;
    }

    public void setAvailableTrips(List<Trip> availableTrips) {
        this.availableTrips = availableTrips;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }
}
