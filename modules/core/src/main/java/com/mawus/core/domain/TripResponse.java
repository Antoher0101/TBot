package com.mawus.core.domain;

import com.mawus.core.entity.Trip;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class TripResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1065600962574702025L;

    private final List<Trip> trips;
    private final Long totalResults;

    public TripResponse(List<Trip> trips, Long totalResults) {
        this.trips = trips;
        this.totalResults = totalResults;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public Long getTotalResults() {
        return totalResults;
    }

    @Override
    public String toString() {
        return "TripResponse{" +
                "trips=" + trips +
                ", totalResults=" + totalResults +
                '}';
    }
}
