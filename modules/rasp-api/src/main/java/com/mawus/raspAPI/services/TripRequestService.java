package com.mawus.raspAPI.services;

import com.mawus.core.domain.TripQuery;
import com.mawus.core.domain.TripResponse;
import com.mawus.core.domain.rasp.scheduleBetStation.ScheduleBetStation;
import com.mawus.core.domain.rasp.scheduleBetStation.Segment;
import com.mawus.core.entity.Transport;
import com.mawus.core.entity.Trip;
import com.mawus.core.service.CityService;
import com.mawus.core.service.TransportService;
import com.mawus.raspAPI.api.APIMethods;
import com.mawus.raspAPI.exceptions.HTTPClientException;
import com.mawus.raspAPI.exceptions.ParserException;
import com.mawus.raspAPI.exceptions.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service("bot_TripRequestService")
public class TripRequestService {
    private final Logger log = LoggerFactory.getLogger(TripRequestService.class);

    private final APIMethods api;
    private final CityService cityService;
    private final TransportService transportService;

    public TripRequestService(APIMethods api, CityService cityService, TransportService transportService) {
        this.api = api;
        this.cityService = cityService;
        this.transportService = transportService;
    }

    public TripResponse fetchNextStations(TripQuery trip, long offset) throws ParserException, ValidationException, HTTPClientException {
        RaspQueryParams queryParams = new RaspQueryParams.Builder()
                .offset(String.valueOf(offset))
                .from(trip.getCityFromTitle())
                .to(trip.getCityToTitle())
                .date(trip.getDate().format(DateTimeFormatter.ISO_DATE))
                .build();

        ScheduleBetStation stations = api.getSchedule(queryParams);
        if (stations == null) {
            log.info("The API did not return any stations");
            return null;
        }
        List<Segment> segments = stations.getSegments();
        List<Trip> trips = segments.stream().map(this::segmentToTrip).toList();
        return new TripResponse(trips, stations.getPagination().getTotal());
    }

    private Trip segmentToTrip(Segment segment) {
        Trip trip = new Trip();
        trip.setCityTo(cityService.findByCode(segment.getTo().getCode()));
        trip.setCityFrom(cityService.findByCode(segment.getFrom().getCode()));
        trip.setTripNumber(segment.getThread().getNumber());
        trip.setDepartureTime(LocalDateTime.parse(segment.getDeparture(), DateTimeFormatter.ISO_DATE_TIME));
        trip.setArrivalTime(LocalDateTime.parse(segment.getArrival(), DateTimeFormatter.ISO_DATE_TIME));
        Transport transport = new Transport();
        transport.setTransportType(transportService.findByCode(segment.getThread().getTransportType()));
        transport.setTile(segment.getThread().getVehicle());
        trip.setTransport(transport);

        return trip;
    }
}
