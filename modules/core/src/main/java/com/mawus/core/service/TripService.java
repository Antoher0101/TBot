package com.mawus.core.service;

import com.mawus.core.entity.Trip;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TripService {

    List<Trip> findByClientId(UUID clientId);

    void saveTrip(Trip trip);

}
