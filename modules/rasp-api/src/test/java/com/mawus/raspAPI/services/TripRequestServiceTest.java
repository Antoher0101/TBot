package com.mawus.raspAPI.services;

import com.mawus.core.CoreApplicationTest;
import com.mawus.core.domain.TripQuery;
import com.mawus.core.domain.TripResponse;
import com.mawus.raspAPI.RaspAPIApplicationTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = {RaspAPIApplicationTest.class, CoreApplicationTest.class})
@ActiveProfiles("test")
class TripRequestServiceTest {
    private final Logger log = LoggerFactory.getLogger(TripRequestServiceTest.class);

    @Autowired
    @Qualifier("bot_TripRequestService")
    private TripRequestService tripRequestService;
    @Test
    public void testFetchNextStationsRealApi() {
        TripQuery trip = new TripQuery();
        trip.setStationFromCode("c213");
        trip.setStationToCode("c2");
        trip.setDate(LocalDate.parse("2024-09-29"));

        try {
            TripResponse trips = tripRequestService.fetchNextStations(trip, 0L, 5L);
            log.info("Total results: {}", trips.getTotalResults());
            assertNotNull(trips);
        } catch (Exception e) {
            fail("Failed to fetch trips from API: " + e.getMessage());
        }
    }
}