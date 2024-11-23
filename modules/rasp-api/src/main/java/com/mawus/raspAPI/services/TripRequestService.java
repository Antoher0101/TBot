package com.mawus.raspAPI.services;

import com.mawus.core.domain.TripQuery;
import com.mawus.core.domain.TripResponse;
import com.mawus.core.domain.rasp.followStations.FollowStations;
import com.mawus.core.domain.rasp.followStations.Stop;
import com.mawus.core.domain.rasp.scheduleBetStation.ScheduleBetStation;
import com.mawus.core.domain.rasp.scheduleBetStation.Segment;
import com.mawus.core.domain.rasp.scheduleStation.Event;
import com.mawus.core.domain.rasp.scheduleStation.Schedule;
import com.mawus.core.domain.rasp.scheduleStation.ScheduleStation;
import com.mawus.core.domain.rasp.scheduleStation.Thread;
import com.mawus.core.entity.Station;
import com.mawus.core.entity.Transport;
import com.mawus.core.entity.Trip;
import com.mawus.core.service.StationService;
import com.mawus.core.service.TransportService;
import com.mawus.raspAPI.api.APIMethods;
import com.mawus.raspAPI.config.RaspAPIConfiguration;
import com.mawus.raspAPI.exceptions.HTTPClientException;
import com.mawus.raspAPI.exceptions.ParserException;
import com.mawus.raspAPI.exceptions.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service("bot_TripRequestService")
public class TripRequestService {
    private final Logger log = LoggerFactory.getLogger(TripRequestService.class);

    private final APIMethods api;
    private final RaspAPIConfiguration configuration;
    private final StationService stationService;
    private final TransportService transportService;

    public TripRequestService(APIMethods api, RaspAPIConfiguration configuration, StationService stationService, TransportService transportService) {
        this.api = api;
        this.configuration = configuration;
        this.stationService = stationService;
        this.transportService = transportService;
    }

    public TripResponse fetchNextStations(TripQuery trip, Long offset, Long limit)
            throws ParserException, ValidationException, HTTPClientException {

        if (trip == null) {
            log.error("The trip query is null.");
            throw new ValidationException("Trip query cannot be null.");
        }

        if (trip.getCityFromTitle() == null || trip.getCityToTitle() == null || trip.getDate() == null) {
            log.error("Invalid trip query parameters: {}", trip);
            throw new ValidationException("Trip query parameters cannot be null.");
        }

        if (offset != null && offset < 0) {
            log.warn("Offset is less than 0. Defaulting to 0.");
            offset = 0L;
        }

        if (limit != null && limit <= 0) {
            log.warn("Limit is less than or equal to 0. Using default query limit: {}", configuration.getQueryLimit());
            limit = (long) configuration.getQueryLimit();
        }

        log.debug("Preparing query parameters for API call. Offset: {}, Limit: {}, Trip: {}", offset, limit, trip);

        RaspQueryParams queryParams = new RaspQueryParams.Builder()
                .offset(String.valueOf(offset))
                .from(trip.getCityFromTitle())
                .transportType(trip.getTransportType())
                .to(trip.getCityToTitle())
                .limit(String.valueOf(limit))
                .date(trip.getDate().format(DateTimeFormatter.ISO_DATE))
                .build();

        ScheduleBetStation stations;
        try {
            stations = api.getSchedule(queryParams);
        } catch (HTTPClientException e) {
            log.error("Failed to fetch schedule from API. Query parameters: {}", queryParams, e);
            throw e;
        }

        if (stations == null) {
            log.info("The API did not return any stations for query: {}", queryParams);
            return null;
        }

        List<Segment> segments = stations.getSegments();
        if (segments.isEmpty()) {
            log.info("No segments found in the response for query: {}", queryParams);
            return new TripResponse(Collections.emptySet(), stations.getPagination().getTotal());
        }

        log.debug("Mapping segments to trips. Segment count: {}", segments.size());

        Set<Trip> trips = segments.stream()
                .map(this::segmentToTrip)
                .collect(Collectors.toSet());

        log.info("Successfully fetched {} trips. Total pagination count: {}", trips.size(), stations.getPagination().getTotal());

        return new TripResponse(trips, stations.getPagination().getTotal());
    }

    private Trip segmentToTrip(Segment segment) {
        if (segment == null) {
            log.warn("Received null segment. Skipping.");
            return null;
        }

        Trip trip = new Trip();
        try {
            Station stationFrom = stationService.findStationByCode(segment.getFrom().getCode());
            Station stationTo = stationService.findStationByCode(segment.getTo().getCode());
            trip.setStationFrom(stationFrom);
            trip.setStationTo(stationTo);

            trip.setTripNumber(segment.getThread().getNumber());
            trip.setDepartureTime(LocalDateTime.parse(segment.getDeparture(), DateTimeFormatter.ISO_DATE_TIME));
            trip.setArrivalTime(LocalDateTime.parse(segment.getArrival(), DateTimeFormatter.ISO_DATE_TIME));

            Transport transport = new Transport();
            transport.setTransportType(transportService.findByCode(segment.getThread().getTransportType()));
            transport.setTitle(segment.getThread().getShortTitle());
            trip.setTransport(transport);

            log.debug("Successfully mapped segment to trip: {}", trip);
        } catch (Exception e) {
            log.error("Failed to map segment to trip. Segment: {}", segment, e);
            throw e;
        }
        return trip;
    }

    public List<Station> getIntermediateStations(Trip trip) throws ParserException, ValidationException, HTTPClientException {
        Optional<String> threadUid = getThreadUid(trip);

        if (threadUid.isEmpty()) {
            log.warn("[getIntermediateStations] threadUid not found for trip: {}", trip);
            return Collections.emptyList();
        }

        RaspQueryParams arrivalQueryParams = new RaspQueryParams.Builder()
                .uid(threadUid.get())
                .from(trip.getStationFrom().getApiCode())
                .to(trip.getStationTo().getApiCode())
                .date(trip.getDepartureTime().format(DateTimeFormatter.ISO_DATE))
                .build();

        log.debug("[getIntermediateStations] Query parameters: {}", arrivalQueryParams);

        FollowStations followStations;
        try {
            followStations = api.getFollowList(arrivalQueryParams);
        } catch (HTTPClientException | ParserException | ValidationException ex) {
            log.error("[getIntermediateStations] Error while fetching follow list for params: {}", arrivalQueryParams, ex);
            throw ex;
        }
        List<Stop> stops = followStations.getStops();
        log.debug("[getIntermediateStations] Retrieved stops: {}", stops);

        int fromIndex = -1;
        int toIndex = -1;

        for (int i = 0; i < stops.size(); i++) {
            Stop stop = stops.get(i);
            if (stop.getStation().getCode().equals(trip.getStationFrom().getApiCode())) {
                fromIndex = i;
            }
            if (stop.getStation().getCode().equals(trip.getStationTo().getApiCode())) {
                toIndex = i;
                break;
            }
        }

        if (fromIndex == -1 || toIndex == -1 || fromIndex >= toIndex) {
            log.warn("[getIntermediateStations] Invalid indices: fromIndex={}, toIndex={}", fromIndex, toIndex);
            return Collections.emptyList();
        }
        List<Stop> intermediateStops = stops.subList(fromIndex, toIndex);
        log.debug("[getIntermediateStations] Intermediate stops: {}", intermediateStops);

        List<Station> intermediateStations = new ArrayList<>();
        for (Stop stop : intermediateStops) {
            String stationCode = stop.getStation().getCode();
            Station station = stationService.findStationByCode(stationCode);
            if (station != null) {
                intermediateStations.add(station);
            } else {
                log.warn("[getIntermediateStations] Station not found for code: {}", stationCode);
            }
        }
        log.info("[getIntermediateStations] Found {} intermediate stations for trip: {}", intermediateStations.size(), trip);

        return intermediateStations;
    }

    private Optional<String> getThreadUid(Trip trip) throws HTTPClientException, ParserException, ValidationException {
        RaspQueryParams departureQueryParams = new RaspQueryParams.Builder()
                .station(trip.getStationFrom().getApiCode())
                .event(Event.DEPARTURE.getId())
                .date(trip.getDepartureTime().format(DateTimeFormatter.ISO_DATE))
                .transportType(trip.getTransport().getTransportType().getCode())
                .build();

        ScheduleStation departureStation = api.getScheduleStation(departureQueryParams);
        Optional<String> threadUid = findThreadUid(departureStation, trip.getTripNumber());
        if (threadUid.isEmpty()) {
            RaspQueryParams arrivalQueryParams = new RaspQueryParams.Builder()
                    .station(trip.getStationTo().getApiCode())
                    .event(Event.ARRIVAL.getId())
                    .date(trip.getArrivalTime().format(DateTimeFormatter.ISO_DATE))
                    .transportType(trip.getTransport().getTransportType().getCode())
                    .build();
            ScheduleStation arrivalStation = api.getScheduleStation(arrivalQueryParams);

            threadUid = findThreadUid(arrivalStation, trip.getTripNumber());
        }
        return threadUid;
    }

    private Optional<String> findThreadUid(ScheduleStation scheduleStation, String threadNumber) {
        if (scheduleStation.getSchedule() == null || scheduleStation.getSchedule().isEmpty()) {
            return Optional.empty();
        }

        return scheduleStation.getSchedule().stream()
                .map(Schedule::getThread)
                .filter(thread -> threadNumber.equals(thread.getNumber()))
                .map(Thread::getUid)
                .findFirst();
    }
}
