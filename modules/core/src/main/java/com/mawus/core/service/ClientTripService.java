package com.mawus.core.service;

import com.mawus.core.domain.ClientTrip;
import com.mawus.core.entity.Trip;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ClientTripService {

    void createDraftTrip(Long chatId, ClientTrip clientTrip);
    void updateTripTransportType(Long chatId, String transportType);
    ClientTrip findTripByChatId(Long chatId);
    void disposeDraftTrip(Long chatId);

    void updateCityDeparture(Long chatId, String cityName);
    void updateCityArrival(Long chatId, String cityName);

    void updateTripDate(Long chatId, LocalDate date);

    void updateCurrentPage(Long chatId, int page);
    int toNextPage(Long chatId);
    int toPrevPage(Long chatId);

    boolean isTripComplete(Long chatId);
}
