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

    public TripResponse fetchNextStations(TripQuery trip, Long offset, Long limit) throws ParserException, ValidationException, HTTPClientException {
        RaspQueryParams queryParams = new RaspQueryParams.Builder()
                .offset(String.valueOf(offset))
                .from(trip.getCityFromTitle())
                .transportType(trip.getTransportType())
                .to(trip.getCityToTitle())
                .limit(String.valueOf(limit != null ? limit : configuration.getQueryLimit()))
                .date(trip.getDate().format(DateTimeFormatter.ISO_DATE))
                .build();

        ScheduleBetStation stations = api.getSchedule(queryParams);
        if (stations == null) {
            log.info("The API did not return any stations");
            return null;
        }
        List<Segment> segments = stations.getSegments();
        Set<Trip> trips = segments.stream().map(this::segmentToTrip).collect(Collectors.toSet());
        return new TripResponse(trips, stations.getPagination().getTotal());
    }

    private Trip segmentToTrip(Segment segment) {
        Trip trip = new Trip();

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

        return trip;
    }

    public List<Station> getIntermediateStations(Trip trip) throws ParserException, ValidationException, HTTPClientException {
        Optional<String> threadUid = getThreadUid(trip);

        if (threadUid.isEmpty()) {
            return null;
        }

        RaspQueryParams arrivalQueryParams = new RaspQueryParams.Builder()
                .uid(threadUid.get())
                .from(trip.getStationFrom().getApiCode())
                .to(trip.getStationTo().getApiCode())
                .date(trip.getDepartureTime().format(DateTimeFormatter.ISO_DATE))
                .build();
        FollowStations followStations = api.getFollowList(arrivalQueryParams);
        List<Stop> stops = followStations.getStops();

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
            return Collections.emptyList();
        }
        List<Stop> intermediateStops = stops.subList(fromIndex, toIndex);

        List<Station> intermediateStations = new ArrayList<>();
        for (Stop stop : intermediateStops) {
            String stationCode = stop.getStation().getCode();
            Station station = stationService.findStationByCode(stationCode);
            if (station != null) {
                intermediateStations.add(station);
            }
        }

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
