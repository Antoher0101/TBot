package com.mawus.raspAPI.listeners;

import com.mawus.core.domain.rasp.stationList.ObjStation;
import com.mawus.core.domain.rasp.stationList.Settlements;
import com.mawus.core.domain.rasp.stationList.StationList;
import com.mawus.core.entity.City;
import com.mawus.core.entity.Station;
import com.mawus.core.entity.enums.StationType;
import com.mawus.core.events.CityUpdateEvent;
import com.mawus.core.service.CityService;
import com.mawus.raspAPI.api.APIMethods;
import com.mawus.raspAPI.exceptions.HTTPClientException;
import com.mawus.raspAPI.exceptions.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CityUpdateEventListener implements ApplicationListener<CityUpdateEvent> {
    private final Logger log = LoggerFactory.getLogger(CityUpdateEventListener.class);

    private final APIMethods api;
    private final CityService cityService;

    public CityUpdateEventListener(APIMethods api, CityService cityService) {
        this.api = api;
        this.cityService = cityService;
    }

    @Override
    public void onApplicationEvent(CityUpdateEvent event) {
        log.info("Update cities from API event received.");

        StationList stationList;
        try {
            stationList = api.getAllowStationsList();
            log.info("Successfully retrieved station list from API.");
        } catch (HTTPClientException e) {
            log.error("HTTP error while retrieving station list from API.", e);
            throw new RuntimeException(e);
        } catch (ParserException e) {
            log.error("Parsing error while retrieving station list from API.", e);
            throw new RuntimeException(e);
        }

        List<City> allCities = stationList.getCountries().stream()
                .filter(c -> c.getTitle().equals("Россия"))
                .flatMap(country -> country.getRegions().stream())
                .flatMap(region -> region.getSettlements().stream())
                .map(this::mapToCity)
                .filter(city -> (StringUtils.hasText(city.getTitle()) && StringUtils.hasText(city.getApiCode())))
                .toList();

        log.info("Prepared {} cities for validation.", allCities.size());

        Set<String> existingCities = cityService.findAll()
                .stream()
                .map(City::getApiCode)
                .collect(Collectors.toSet());

        log.info("Loaded {} existing cities from the database.", existingCities.size());

        List<City> newCities = allCities.stream()
                .filter(city -> !existingCities.contains(city.getApiCode()))
                .toList();

        if (!newCities.isEmpty()) {
            cityService.saveCityWithStations(newCities);
            log.info("Successfully saved {} new cities to the database.", newCities.size());
        } else {
            log.info("No new cities to save. All cities are already in the database.");
        }
    }

    private City mapToCity(Settlements settlements) {
        City city = new City();
        city.setTitle(settlements.getTitle());
        city.setApiCode(settlements.getCodes().getYandexCode());

        Set<Station> stations = mapStations(settlements);
        city.setStations(stations);

        return city;
    }

    private Set<Station> mapStations(Settlements settlement) {
        return settlement.getStations().stream()
                .map(this::mapToStation)
                .collect(Collectors.toSet());
    }

    private Station mapToStation(ObjStation stationsApi) {
        Station station = new Station();
        station.setName(stationsApi.getTitle());
        station.setApiCode(stationsApi.getCodes().getYandexCode());
        station.setType(StationType.fromId(stationsApi.getStationType()));
        return station;
    }
}
