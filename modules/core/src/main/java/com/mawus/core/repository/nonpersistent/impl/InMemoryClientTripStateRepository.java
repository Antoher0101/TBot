package com.mawus.core.repository.nonpersistent.impl;

import com.mawus.core.domain.ClientTrip;
import com.mawus.core.entity.Transport;
import com.mawus.core.entity.TransportType;
import com.mawus.core.entity.Trip;
import com.mawus.core.repository.nonpersistent.ClientTripStateRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.time.LocalDateTime;
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
    public void updateTripTransportType(Long chatId, TransportType transportType) {
        Trip trip = clientsTrip.get(chatId).getTrip();
        Transport transport = trip.getTransport();
        if (transport != null) {
            trip.getTransport().setTransportType(transportType);
        } else {
            transport = new Transport();
            transport.setTransportType(transportType);
        }
        trip.setTransport(transport);
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
    public void updateCityDeparture(Long chatId, String cityName) {
        Trip trip = clientsTrip.get(chatId).getTrip();
        trip.setCityFrom(cityName);
    }

    @Override
    public void updateCityArrival(Long chatId, String cityName) {
        Trip trip = clientsTrip.get(chatId).getTrip();
        trip.setCityTo(cityName);
    }

    @Override
    public void updateTripDate(Long chatId, LocalDateTime date) {
        Trip trip = clientsTrip.get(chatId).getTrip();
        trip.setDepartureTime(date);
    }

    @Override
    public boolean isTripComplete(Long chatId) {
        Trip trip = clientsTrip.get(chatId).getTrip();
        return false;
    }
}
