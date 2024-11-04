package com.mawus.core.service.impl;

import com.mawus.core.entity.Trip;
import com.mawus.core.repository.TransportRepository;
import com.mawus.core.repository.TripRepository;
import com.mawus.core.service.TripService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TripServiceImpl implements TripService {

    @PersistenceContext
    private EntityManager entityManager;

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
    public long countTripsByClientId(UUID clientId) {
        return tripRepository.countByClient_Id(clientId);
    }

    @Override
    public List<Trip> findWithIntermediateStationsByClientId(UUID clientId) {
        return tripRepository.findFullTripsByClient_Id(clientId);
    }

    @Override
    public List<Trip> findCompanions(Trip trip) {
        return tripRepository.findCompanionTrips(trip);
    }

    @Override
    public List<Trip> findCompanionsPage(Trip trip, Pageable pageable) {
        return tripRepository.findPageableCompanionTrips(trip, pageable);
    }

    @Override
    @Transactional
    public Trip loadIntermediateStations(Trip trip) {
        Session session = entityManager.unwrap(Session.class);
        Trip t = session.get(Trip.class, trip.getId());
        Hibernate.initialize(t.getIntermediateStations());
        return t;
    }
}
