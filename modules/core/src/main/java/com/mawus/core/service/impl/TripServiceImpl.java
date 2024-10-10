package com.mawus.core.service.impl;

import com.mawus.core.entity.Trip;
import com.mawus.core.repository.TransportRepository;
import com.mawus.core.repository.TripRepository;
import com.mawus.core.service.TripService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TransportRepository transportRepository;

    public TripServiceImpl(TripRepository tripRepository,
                           TransportRepository transportRepository) {
        this.tripRepository = tripRepository;
        this.transportRepository = transportRepository;
    }

    @Override
    public List<Trip> findByClientId(UUID clientId) {
        return tripRepository.findByClientId(clientId);
    }

    @Override
    public List<Trip> findByClientId(UUID clientId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return tripRepository.findByClient_Id(clientId, pageable);
    }

    @Override
    public void saveTrip(Trip trip) {
        if (trip.getTransport().getId() == null) {
            transportRepository.save(trip.getTransport());
        }

        tripRepository.save(trip);
    }

    @Override
    public long countTripsByClient(UUID clientId) {
        return tripRepository.countByClient_Id(clientId);
    }
}
