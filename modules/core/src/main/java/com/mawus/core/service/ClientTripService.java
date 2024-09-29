package com.mawus.core.service;

import com.mawus.core.domain.ClientTrip;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ClientTripService {

    void createDraftTrip(Long chatId, ClientTrip clientTrip);
    void updateTripTransportType(Long chatId, String transportType);
    ClientTrip findTripByChatId(Long chatId);
    void disposeDraftTrip(Long chatId);

    void updateCityDeparture(Long chatId, String cityName);
    void updateCityArrival(Long chatId, String cityName);

    void updateTripDate(Long chatId, LocalDate date);

    boolean isTripComplete(Long chatId);

}
