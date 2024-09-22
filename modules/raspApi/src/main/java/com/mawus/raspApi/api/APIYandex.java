package com.mawus.raspApi.api;

import com.mawus.core.domain.rasp.followStations.FollowStations;
import com.mawus.core.domain.rasp.infoCarrier.InfoCarrier;
import com.mawus.core.domain.rasp.nearCity.NearCity;
import com.mawus.core.domain.rasp.nearStations.NearStations;
import com.mawus.core.domain.rasp.scheduleBetStation.ScheduleBetStation;
import com.mawus.core.domain.rasp.scheduleStation.ScheduleStation;
import com.mawus.core.domain.rasp.stationList.StationList;
import com.mawus.raspApi.config.RaspApiConfiguration;
import com.mawus.raspApi.exceptions.FieldHolder;
import com.mawus.raspApi.exceptions.HTTPClientException;
import com.mawus.raspApi.exceptions.ParserException;
import com.mawus.raspApi.exceptions.ValidationException;
import com.mawus.raspApi.services.APIConnector;
import com.mawus.raspApi.services.JSONParser;
import com.mawus.raspApi.services.ParamBuilder;
import com.mawus.raspApi.services.RequestBuilder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class APIYandex implements APIMethods {

    private final APIConnector APICon;
    private final RequestBuilder request;
    private JSONParser jsonParser;

    public APIYandex(RaspApiConfiguration configuration) throws ValidationException {
        APICon = new APIConnector(configuration.getApiKey());
        request = new RequestBuilder(configuration.getApiLink());
    }

    @Override
    public ScheduleBetStation getSchedule(ParamBuilder params)
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

        return jsonParser.parseIntoObject(ScheduleBetStation.class, request.getRequest(), APICon, time);
    }


    @Override
    public ScheduleStation getScheduleStation(ParamBuilder params)
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

        return jsonParser.parseIntoObject(ScheduleStation.class, request.getRequest(), APICon, time);
    }

    @Override
    public FollowStations getFollowList(ParamBuilder params)
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

        return jsonParser.parseIntoObject(FollowStations.class, request.getRequest(), APICon, time);
    }

    @Override
    public NearStations getNearStations(ParamBuilder params)
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

        return jsonParser.parseIntoObject(NearStations.class, request.getRequest(), APICon, time);
    }

    @Override
    public NearCity getNearCity(ParamBuilder params)
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

        return jsonParser.parseIntoObject(NearCity.class, request.getRequest(), APICon, time);
    }

    @Override
    public InfoCarrier getInfoCarrier(ParamBuilder params)
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

        return jsonParser.parseIntoObject(InfoCarrier.class, request.getRequest(), APICon, time);
    }

    @Override
    public StationList getAllowStationsList() throws HTTPClientException, ParserException {
        request.setBranch("/stations_list/?");

        jsonParser = new JSONParser();
        Duration time = Duration.ofMinutes(10);

        return jsonParser.parseIntoObject(StationList.class, request.getRequest(), APICon, time);
    }
}
