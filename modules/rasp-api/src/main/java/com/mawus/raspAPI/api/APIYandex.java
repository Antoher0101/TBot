package com.mawus.raspAPI.api;

import com.mawus.core.domain.rasp.followStations.FollowStations;
import com.mawus.core.domain.rasp.infoCarrier.InfoCarrier;
import com.mawus.core.domain.rasp.nearCity.NearCity;
import com.mawus.core.domain.rasp.nearStations.NearStations;
import com.mawus.core.domain.rasp.scheduleBetStation.ScheduleBetStation;
import com.mawus.core.domain.rasp.scheduleStation.ScheduleStation;
import com.mawus.core.domain.rasp.stationList.StationList;
import com.mawus.raspAPI.config.RaspAPIConfiguration;
import com.mawus.raspAPI.exceptions.FieldHolder;
import com.mawus.raspAPI.exceptions.HTTPClientException;
import com.mawus.raspAPI.exceptions.ParserException;
import com.mawus.raspAPI.exceptions.ValidationException;
import com.mawus.raspAPI.services.APIConnector;
import com.mawus.raspAPI.services.JSONParser;
import com.mawus.raspAPI.services.RaspQueryParams;
import com.mawus.raspAPI.services.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component("rasp_APIYandex")
public class APIYandex implements APIMethods {
    private final Logger log = LoggerFactory.getLogger(APIYandex.class);

    private final RaspAPIConfiguration configuration;
    private final APIConnector APICon;
    private final RequestBuilder request;
    private JSONParser jsonParser;

    public APIYandex(RaspAPIConfiguration configuration) throws ValidationException {
        APICon = new APIConnector(configuration.getApiKey());
        request = new RequestBuilder(configuration.getApiLink());

        this.configuration = configuration;
    }

    @Override
    public ScheduleBetStation getSchedule(RaspQueryParams params)
            throws HTTPClientException, ParserException, ValidationException {
        request.setBranch("/search/?");

        if (params.getTo().isEmpty() || params.getFrom().isEmpty()) {

            FieldHolder holder = new FieldHolder("to", params.getTo());
            FieldHolder holder1 = new FieldHolder("from", params.getFrom());
            List<FieldHolder> holders = new ArrayList<>();
            holders.add(holder);
            holders.add(holder1);

            throw new ValidationException("Не указан параметр to или from", holders);
        }

        request.addParams(params);
        jsonParser = new JSONParser();
        Duration time = Duration.ofMinutes(2);

        log.debug("[getSchedule] Query: {}", request.getRequest());

        return jsonParser.deserializeToObject(ScheduleBetStation.class, request.getRequest(), APICon, time);
    }


    @Override
    public ScheduleStation getScheduleStation(RaspQueryParams params)
            throws HTTPClientException, ParserException, ValidationException {
        request.setBranch("/schedule/?");

        if (params.getStation().isEmpty()) {

            FieldHolder holder = new FieldHolder("station", params.getStation());
            List<FieldHolder> holders = new ArrayList<>();
            holders.add(holder);

            throw new ValidationException("Не указан параметр station", holders);
        }

        request.addParams(params);

        jsonParser = new JSONParser();
        Duration time = Duration.ofMinutes(2);

        log.debug("[getScheduleStation] Query: {}", request.getRequest());

        return jsonParser.deserializeToObject(ScheduleStation.class, request.getRequest(), APICon, time);
    }

    @Override
    public FollowStations getFollowList(RaspQueryParams params)
            throws HTTPClientException, ParserException, ValidationException {
        request.setBranch("/thread/?");

        if (params.getUid().isEmpty()) {
            FieldHolder holder = new FieldHolder("uid", params.getUid());
            List<FieldHolder> holders = new ArrayList<>();
            holders.add(holder);

            throw new ValidationException("Не указан параметр uid", holders);
        }

        request.addParams(params);

        jsonParser = new JSONParser();
        Duration time = Duration.ofMinutes(2);

        log.debug("[getFollowList] Query: {}", request.getRequest());

        return jsonParser.deserializeToObject(FollowStations.class, request.getRequest(), APICon, time);
    }

    @Override
    public NearStations getNearStations(RaspQueryParams params)
            throws HTTPClientException, ParserException, ValidationException {
        request.setBranch("/nearest_stations/?");

        if (params.getLatitude().isEmpty() || params.getLongitude().isEmpty() || params.getDistance()
                .isEmpty()) {
            FieldHolder holder = new FieldHolder("lat", params.getLatitude());
            FieldHolder holder1 = new FieldHolder("lng", params.getLongitude());
            FieldHolder holder2 = new FieldHolder("distance", params.getDistance());

            List<FieldHolder> holders = new ArrayList<>();

            holders.add(holder);
            holders.add(holder1);
            holders.add(holder2);

            throw new ValidationException("Не указан параметры lat, lng, distance", holders);
        }

        request.addParams(params);

        jsonParser = new JSONParser();
        Duration time = Duration.ofMinutes(2);

        log.debug("[getNearStations] Query: {}", request.getRequest());

        return jsonParser.deserializeToObject(NearStations.class, request.getRequest(), APICon, time);
    }

    @Override
    public NearCity getNearCity(RaspQueryParams params)
            throws HTTPClientException, ParserException, ValidationException {
        request.setBranch("/nearest_settlement/?");

        if (params.getLongitude().isEmpty() || params.getLatitude().isEmpty()) {

            FieldHolder holder = new FieldHolder("lat", params.getLatitude());
            FieldHolder holder1 = new FieldHolder("lng", params.getLongitude());

            List<FieldHolder> holders = new ArrayList<>();

            holders.add(holder);
            holders.add(holder1);

            throw new ValidationException("Не указан параметры lat, lng", holders);
        }

        request.addParams(params);

        jsonParser = new JSONParser();
        Duration time = Duration.ofMinutes(2);

        log.debug("[getNearCity] Query: {}", request.getRequest());

        return jsonParser.deserializeToObject(NearCity.class, request.getRequest(), APICon, time);
    }

    @Override
    public InfoCarrier getInfoCarrier(RaspQueryParams params)
            throws HTTPClientException, ParserException, ValidationException {
        request.setBranch("/carrier/?");

        if (params.getCode().isEmpty()) {
            FieldHolder holder = new FieldHolder("code", params.getCode());
            List<FieldHolder> holders = new ArrayList<>();

            holders.add(holder);

            throw new ValidationException("Не указан параметр code", holders);
        }

        request.addParams(params);

        jsonParser = new JSONParser();
        Duration time = Duration.ofMinutes(2);

        log.debug("[getInfoCarrier] Query: {}", request.getRequest());

        return jsonParser.deserializeToObject(InfoCarrier.class, request.getRequest(), APICon, time);
    }

    @Override
    public StationList getAllowStationsList() throws HTTPClientException, ParserException {
        request.setBranch("/stations_list/?");

        jsonParser = new JSONParser();
        Duration time = Duration.ofMinutes(10);

        log.debug("[getAllowStationsList] Query: {}", request.getRequest());

        return jsonParser.deserializeToObject(StationList.class, request.getRequest(), APICon, time);
    }
}
