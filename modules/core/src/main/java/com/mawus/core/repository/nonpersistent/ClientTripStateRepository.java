package com.mawus.core.repository.nonpersistent;

import com.mawus.core.domain.ClientTrip;

import java.time.LocalDate;

public interface ClientTripStateRepository {

    void createDraftTrip(Long chatId, ClientTrip clientTrip);

    void updateTripTransportType(Long chatId, String transportType);

    ClientTrip findTripByChatId(Long chatId);

    void disposeDraftTrip(Long chatId);

    void updateCityDeparture(Long chatId, String cityCode);

    void updateCityArrival(Long chatId, String cityCode);

    void updateTripDate(Long chatId, LocalDate date);

    boolean isTripComplete(Long chatId);

    void updateCurrentPage(Long chatId, int page);

    int toNextPage(Long chatId);

    int toPrevPage(Long chatId);
}
