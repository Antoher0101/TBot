package com.mawus.raspAPI.services;

import com.mawus.core.domain.TripQuery;
import com.mawus.core.entity.Trip;
import com.mawus.raspAPI.RaspAPIApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@ContextConfiguration(classes = {RaspAPIApplicationTest.class})
@ActiveProfiles("test")
class TripRequestServiceTest {

    @Autowired
    @Qualifier("bot_TripRequestService")
    private TripRequestService tripRequestService;
    @Test
    public void testFetchNextStationsRealApi() {
        TripQuery trip = new TripQuery();
        trip.setCityFromTitle("c213");
        trip.setCityToTitle("c53");
        trip.setDate(LocalDate.parse("2024-09-24"));

        try {
            List<Trip> trips = tripRequestService.fetchNextStations(trip, 0L);
            String message = trips.toString();
            assertNotNull(trips, message);
        } catch (Exception e) {
            fail("Failed to fetch trips from API: " + e.getMessage());
        }
    }
}