package com.mawus.raspAPI.api;

import com.mawus.core.domain.rasp.followStations.FollowStations;
import com.mawus.core.domain.rasp.infoCarrier.InfoCarrier;
import com.mawus.core.domain.rasp.nearCity.NearCity;
import com.mawus.core.domain.rasp.nearStations.NearStations;
import com.mawus.core.domain.rasp.scheduleBetStation.ScheduleBetStation;
import com.mawus.core.domain.rasp.scheduleStation.ScheduleStation;
import com.mawus.core.domain.rasp.stationList.StationList;
import com.mawus.raspAPI.exceptions.HTTPClientException;
import com.mawus.raspAPI.exceptions.ParserException;
import com.mawus.raspAPI.exceptions.ValidationException;
import com.mawus.raspAPI.services.RaspQueryParams;

public interface APIMethods {

    ScheduleBetStation getSchedule(RaspQueryParams params)
            throws HTTPClientException, ParserException, ValidationException;

    ScheduleStation getScheduleStation(RaspQueryParams params)
            throws HTTPClientException, ParserException, ValidationException;

    FollowStations getFollowList(RaspQueryParams params)
            throws HTTPClientException, ParserException, ValidationException;

    NearStations getNearStations(RaspQueryParams params)
            throws HTTPClientException, ParserException, ValidationException;

    NearCity getNearCity(RaspQueryParams params)
            throws HTTPClientException, ParserException, ValidationException;

    InfoCarrier getInfoCarrier(RaspQueryParams params)
            throws HTTPClientException, ParserException, ValidationException;

    StationList getAllowStationsList()
            throws HTTPClientException, ParserException;
}
