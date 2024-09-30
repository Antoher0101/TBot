package com.mawus.core.domain;

import com.mawus.core.entity.Trip;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class TripResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1065600962574702025L;

    private final Set<Trip> trips;
    private final Long totalResults;

    public TripResponse(Set<Trip> trips, Long totalResults) {
        this.trips = trips;
        this.totalResults = totalResults;
    }

    public Set<Trip> getTrips() {
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
