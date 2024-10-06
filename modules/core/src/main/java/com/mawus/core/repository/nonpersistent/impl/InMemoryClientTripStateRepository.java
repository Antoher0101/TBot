package com.mawus.core.repository.nonpersistent.impl;

import com.mawus.core.domain.ClientTrip;
import com.mawus.core.domain.TripQuery;
import com.mawus.core.entity.Trip;
import com.mawus.core.repository.nonpersistent.ClientTripStateRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryClientTripStateRepository implements ClientTripStateRepository {

    private final Map<Long, ClientTrip> clientsTrip = new ConcurrentHashMap<>();

    @Override
    public void createDraftTrip(Long chatId, ClientTrip clientTrip) {
        clientsTrip.put(chatId, SerializationUtils.clone(clientTrip));
    }

    @Override
    public void updateTripTransportType(Long chatId, String transportType) {
        TripQuery trip = clientsTrip.get(chatId).getTripQuery();
        trip.setTransportType(transportType);
    }

    @Override
    public ClientTrip findTripByChatId(Long chatId) {
        ClientTrip clientTrip = clientsTrip.get(chatId);
        if (clientTrip == null) {
            return null;
        }
        return SerializationUtils.clone(clientTrip);
    }

    @Override
    public void disposeDraftTrip(Long chatId) {
        clientsTrip.remove(chatId);
    }

    @Override
    public void updateCityDeparture(Long chatId, String cityCode) {
        TripQuery trip = clientsTrip.get(chatId).getTripQuery();
        trip.setCityFromTitle(cityCode);
    }

    @Override
    public void updateCityArrival(Long chatId, String cityCode) {
        TripQuery trip = clientsTrip.get(chatId).getTripQuery();
        trip.setCityToTitle(cityCode);
    }

    @Override
    public void updateTripDate(Long chatId, LocalDate date) {
        TripQuery trip = clientsTrip.get(chatId).getTripQuery();
        trip.setDate(date);
    }

    @Override
    public void updateCurrentPage(Long chatId, int page) {
        ClientTrip clientTrip = clientsTrip.get(chatId);
        clientTrip.setCurrentPage(page);
    }

    @Override
    public int toNextPage(Long chatId) {
        ClientTrip clientTrip = clientsTrip.get(chatId);
        int nextPage = clientTrip.getCurrentPage() + 1;
        clientTrip.setCurrentPage(nextPage);
        return nextPage;
    }

    @Override
    public int toPrevPage(Long chatId) {
        ClientTrip clientTrip = clientsTrip.get(chatId);
        int prevPage = clientTrip.getCurrentPage() - 1;
        clientTrip.setCurrentPage(prevPage);
        return prevPage;
    }

    @Override
    public void updateTripOffset(Long chatId, Long newOffset) {
        ClientTrip clientTrip = clientsTrip.get(chatId);
        if (clientTrip != null) {
            clientTrip.setOffset(newOffset);
        }
    }

    @Override
    public void updateAvailableTrips(Long chatId, List<Trip> newTrips) {
        ClientTrip clientTrip = clientsTrip.get(chatId);
        if (clientTrip != null) {
            clientTrip.setAvailableTrips(new ArrayList<>(newTrips));
        }
    }

    @Override
    public void setTrip(Long chatId, Trip trip) {
        ClientTrip clientTrip = clientsTrip.get(chatId);
        if (clientTrip != null) {
            clientTrip.setTrip(trip);
        }
    }

    @Override
    public Trip getTrip(Long chatId) {
        ClientTrip clientTrip = clientsTrip.get(chatId);
        if (clientTrip != null) {
            return clientTrip.getTrip();
        }
        return null;
    }
}
