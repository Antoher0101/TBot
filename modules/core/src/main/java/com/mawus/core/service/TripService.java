package com.mawus.core.service;

import com.mawus.core.entity.Trip;

import java.util.List;
import java.util.UUID;

public interface TripService {

    List<Trip> findByClientId(UUID clientId);
    List<Trip> findWithIntermediateStationsByClientId(UUID clientId);
    List<Trip> findByClientId(UUID clientId, int page, int size);

    void saveTrip(Trip trip);

    long countTripsByClientId(UUID clientId);

    List<Trip> findCompanions(Trip trip);
}
