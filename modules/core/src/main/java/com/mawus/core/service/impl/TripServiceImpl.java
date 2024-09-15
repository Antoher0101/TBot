package com.mawus.core.service.impl;

import com.mawus.core.entity.Trip;
import com.mawus.core.repository.TripRepository;
import com.mawus.core.service.TripService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    public TripServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public List<Trip> findByClientId(UUID clientId) {
        return tripRepository.findByClientId(clientId);
    }

    @Override
    public void saveTrip(Trip trip) {
        tripRepository.save(trip);
    }

    @Override
    public void update(Trip trip) {

    }
}
