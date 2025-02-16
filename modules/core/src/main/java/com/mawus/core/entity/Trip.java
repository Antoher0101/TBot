package com.mawus.core.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "bot$Trip")
@Table(name = "bot_trip")
public class Trip extends StandardEntity {
    @Column(name = "trip_number", nullable = false)
    private String tripNumber;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "transport_id")
    private Transport transport;

    @ManyToOne(cascade = CascadeType.DETACH, optional = false)
    @JoinColumn(name = "station_from_id", nullable = false)
    private Station stationFrom;

    @ManyToOne(cascade = CascadeType.DETACH, optional = false)
    @JoinColumn(name = "station_to_id", nullable = false)
    private Station stationTo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "bot_trip_intermediate_station",
            joinColumns = {@JoinColumn(name = "trip_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "station_id", referencedColumnName = "id")})
    private List<Station> intermediateStations;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    @Column(name = "api_link")
    private String apiLink;

    public Station getStationFrom() {
        return stationFrom;
    }

    public void setStationFrom(Station stationFrom) {
        this.stationFrom = stationFrom;
    }

    public Station getStationTo() {
        return stationTo;
    }

    public void setStationTo(Station stationTo) {
        this.stationTo = stationTo;
    }

    public String getTripNumber() {
        return tripNumber;
    }

    public void setTripNumber(String tripNumber) {
        this.tripNumber = tripNumber;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public List<Station> getIntermediateStations() {
        return intermediateStations;
    }

    public void setIntermediateStations(List<Station> intermediateStations) {
        this.intermediateStations = intermediateStations;
    }

    public String getApiLink() {
        return apiLink;
    }

    public void setApiLink(String apiLink) {
        this.apiLink = apiLink;
    }
}
