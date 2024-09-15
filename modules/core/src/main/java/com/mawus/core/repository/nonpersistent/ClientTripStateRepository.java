package com.mawus.core.repository.nonpersistent;

import com.mawus.core.domain.ClientTrip;
import com.mawus.core.entity.TransportType;

public interface ClientTripStateRepository {

    void createDraftTrip(Long chatId, ClientTrip clientTrip);
    void updateTripTransportType(Long chatId, TransportType transportType);
    ClientTrip findTripByChatId(Long chatId);
    void discardDraftTrip(Long chatId);
}
