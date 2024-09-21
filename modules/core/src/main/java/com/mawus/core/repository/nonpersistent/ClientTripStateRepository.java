package com.mawus.core.repository.nonpersistent;

import com.mawus.core.domain.ClientTrip;
import com.mawus.core.entity.TransportType;

import java.time.LocalDateTime;

public interface ClientTripStateRepository {

    void createDraftTrip(Long chatId, ClientTrip clientTrip);
    void updateTripTransportType(Long chatId, TransportType transportType);
    ClientTrip findTripByChatId(Long chatId);
    void disposeDraftTrip(Long chatId);

    void updateCityDeparture(Long chatId, String cityName);
    void updateCityArrival(Long chatId, String cityName);

    void updateTripDate(Long chatId, LocalDateTime date);

    boolean isTripComplete(Long chatId);
}
