package com.mawus.api.dto;

import com.mawus.core.entity.City;
import com.mawus.core.entity.Transport;
import com.mawus.core.entity.Trip;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO for {@link Trip}
 */
public class TripDto implements Serializable {
    private final UUID id;
    private final String transportNumber;
    private final Transport transport;
    private final City cityFrom;
    private final City cityTo;
    private final LocalDateTime departureTime;
    private final LocalDateTime arrivalTime;

    public TripDto(UUID id, String transportNumber, Transport transport, City cityFrom, City cityTo, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        this.id = id;
        this.transportNumber = transportNumber;
        this.transport = transport;
        this.cityFrom = cityFrom;
        this.cityTo = cityTo;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public UUID getId() {
        return id;
    }

    public String getTransportNumber() {
        return transportNumber;
    }

    public Transport getTransport() {
        return transport;
    }

    public City getCityFrom() {
        return cityFrom;
    }

    public City getCityTo() {
        return cityTo;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripDto entity = (TripDto) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.transportNumber, entity.transportNumber) &&
                Objects.equals(this.transport, entity.transport) &&
                Objects.equals(this.cityFrom, entity.cityFrom) &&
                Objects.equals(this.cityTo, entity.cityTo) &&
                Objects.equals(this.departureTime, entity.departureTime) &&
                Objects.equals(this.arrivalTime, entity.arrivalTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, transportNumber, transport, cityFrom, cityTo, departureTime, arrivalTime);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "transportNumber = " + transportNumber + ", " +
                "transport = " + transport + ", " +
                "cityFrom = " + cityFrom + ", " +
                "cityTo = " + cityTo + ", " +
                "departureTime = " + departureTime + ", " +
                "arrivalTime = " + arrivalTime + ")";
    }
}