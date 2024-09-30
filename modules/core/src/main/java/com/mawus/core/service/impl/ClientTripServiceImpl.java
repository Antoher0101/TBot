package com.mawus.core.service.impl;

import com.mawus.core.domain.ClientTrip;
import com.mawus.core.entity.City;
import com.mawus.core.entity.Trip;
import com.mawus.core.repository.nonpersistent.ClientTripStateRepository;
import com.mawus.core.service.CityService;
import com.mawus.core.service.ClientTripService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service("bot_ClientTripServiceImpl")
public class ClientTripServiceImpl implements ClientTripService {

    private final ClientTripStateRepository clientTripStateRepository;
    private final CityService cityService;

    public ClientTripServiceImpl(ClientTripStateRepository clientTripStateRepository, CityService cityService) {
        this.clientTripStateRepository = clientTripStateRepository;
        this.cityService = cityService;
    }

    @Override
    public void createDraftTrip(Long chatId, ClientTrip clientTrip) {
        clientTripStateRepository.createDraftTrip(chatId, clientTrip);
    }

    @Override
    public void updateTripTransportType(Long chatId, String transportType) {
        clientTripStateRepository.updateTripTransportType(chatId, transportType);
    }

    @Override
    public ClientTrip findTripByChatId(Long chatId) {
        return clientTripStateRepository.findTripByChatId(chatId);
    }

    @Override
    public void disposeDraftTrip(Long chatId) {
        clientTripStateRepository.disposeDraftTrip(chatId);
    }

    @Override
    public void updateCityDeparture(Long chatId, String cityName) {
        City city = cityService.findByTitle(cityName);
        if (city == null) {
            throw new RuntimeException("Not found departure city name: " + cityName);
        }
        clientTripStateRepository.updateCityDeparture(chatId, city.getApiCode());
    }

    @Override
    public void updateCityArrival(Long chatId, String cityName) {
        City city = cityService.findByTitle(cityName);
        if (city == null) {
            throw new RuntimeException("Not found arrival city name: " + cityName);
        }
        clientTripStateRepository.updateCityArrival(chatId, city.getApiCode());
    }

    @Override
    public void updateTripDate(Long chatId, LocalDate date) {
        clientTripStateRepository.updateTripDate(chatId, date);
    }

    @Override
    public boolean isTripComplete(Long chatId) {
        return clientTripStateRepository.isTripComplete(chatId);
    }

    @Override
    public List<Trip> nextTrips(Long chatId) {
        return null;
    }

    @Override
    public List<Trip> previousTrips(Long chatId) {
        return null;
    }
}
