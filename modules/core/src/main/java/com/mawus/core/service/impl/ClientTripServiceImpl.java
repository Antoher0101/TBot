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
        City city = cityService.findByTitleElastic(cityName);
        if (city == null) {
            throw new RuntimeException("Not found departure city name: " + cityName);
        }
        clientTripStateRepository.updateCityDeparture(chatId, city.getApiCode());
    }

    @Override
    public void updateCityArrival(Long chatId, String cityName) {
        City city = cityService.findByTitleElastic(cityName);
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
    public void updateCurrentPage(Long chatId, int page) {
        clientTripStateRepository.updateCurrentPage(chatId, page);
    }

    @Override
    public int toNextPage(Long chatId) {
        return clientTripStateRepository.toNextPage(chatId);
    }

    @Override
    public int toPrevPage(Long chatId) {
        return clientTripStateRepository.toPrevPage(chatId);
    }

    @Override
    public void updateTripOffset(Long chatId, Long newOffset) {
        clientTripStateRepository.updateTripOffset(chatId, newOffset);
    }

    @Override
    public void updateAvailableTrips(Long chatId, List<Trip> trips) {
        clientTripStateRepository.updateAvailableTrips(chatId, trips);
    }

    @Override
    public void setTrip(Long chatId, Trip trip) {
        clientTripStateRepository.setTrip(chatId, trip);
    }

    @Override
    public Trip getTrip(Long chatId) {
        return clientTripStateRepository.getTrip(chatId);
    }
}
