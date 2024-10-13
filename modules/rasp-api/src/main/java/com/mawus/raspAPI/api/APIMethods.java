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

    /**
     * <b>Расписание рейсов между станциями</b>
     * @return {@link ScheduleBetStation} список рейсов, следующих от указанной станции отправления
     * к указанной станции прибытия и информацию по каждому рейсу
     */
    ScheduleBetStation getSchedule(RaspQueryParams params)
            throws HTTPClientException, ParserException, ValidationException;

    /**
     * <b>Расписание рейсов по станции</b>
     * @return {@link ScheduleStation} список рейсов, отправляющихся от указанной станции
     * и информацию по каждому рейсу.
     */
    ScheduleStation getScheduleStation(RaspQueryParams params)
            throws HTTPClientException, ParserException, ValidationException;

    /**
     * <b>Список станций следования</b>
     * @return {@link FollowStations} список станций следования нитки по указанному идентификатору нитки,
     * информацию о каждой нитке и о промежуточных станциях нитки.
     */
    FollowStations getFollowList(RaspQueryParams params)
            throws HTTPClientException, ParserException, ValidationException;

    /**
     * <b>Список ближайших станций</b>
     * @return {@link NearStations} список станций, находящихся в указанном радиусе от указанной точки.
     * Максимальное количество возвращаемых станций — 50.
     */
    NearStations getNearStations(RaspQueryParams params)
            throws HTTPClientException, ParserException, ValidationException;

    /**
     * <b>Ближайший город</b>
     * @return {@link NearCity} информация о ближайшем к указанной точке городе
     */
    NearCity getNearCity(RaspQueryParams params)
            throws HTTPClientException, ParserException, ValidationException;

    /**
     * <b>Информация о перевозчике</b>
     * @return {@link InfoCarrier} информация о перевозчике по указанному коду перевозчика.
     */
    InfoCarrier getInfoCarrier(RaspQueryParams params)
            throws HTTPClientException, ParserException, ValidationException;

    /**
     * <b>Список всех доступных станций</b>
     * @return {@link StationList} полный список станций, информацию о которых предоставляют Яндекс Расписания.
     * Список структурирован географически:
     * ответ содержит список стран со вложенными списками регионов и населенных пунктов, в которых находятся станции.
     */
    StationList getAllowStationsList()
            throws HTTPClientException, ParserException;
}
