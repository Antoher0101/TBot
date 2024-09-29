package com.mawus.core.repository.nonpersistent.impl;

import com.mawus.core.domain.ClientTrip;
import com.mawus.core.domain.TripQuery;
import com.mawus.core.repository.nonpersistent.ClientTripStateRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.time.LocalDate;
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
    public boolean isTripComplete(Long chatId) {
        TripQuery trip = clientsTrip.get(chatId).getTripQuery();
        return false;
    }
}
