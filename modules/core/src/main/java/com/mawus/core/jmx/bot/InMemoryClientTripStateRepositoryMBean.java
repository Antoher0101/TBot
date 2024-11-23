package com.mawus.core.jmx.bot;

public interface InMemoryClientTripStateRepositoryMBean {

    String getAllTrips();

    String getTrip(Long clientId);

    String removeTrip(Long clientId);

    String getTripCount();
}
