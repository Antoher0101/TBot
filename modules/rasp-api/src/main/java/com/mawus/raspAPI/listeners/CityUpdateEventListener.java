package com.mawus.raspAPI.listeners;

import com.mawus.core.domain.rasp.stationList.StationList;
import com.mawus.core.entity.City;
import com.mawus.core.events.CityUpdateScheduleEvent;
import com.mawus.core.service.CityService;
import com.mawus.raspAPI.api.APIYandex;
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
public class CityUpdateEventListener implements ApplicationListener<CityUpdateScheduleEvent> {
    private final Logger log = LoggerFactory.getLogger(CityUpdateEventListener.class);

    private final APIYandex apiYandex;
    private final CityService cityService;

    public CityUpdateEventListener(APIYandex apiYandex, CityService cityService) {
        this.apiYandex = apiYandex;
        this.cityService = cityService;
    }

    @Override
    public void onApplicationEvent(CityUpdateScheduleEvent event) {
        log.info("Update cities from API event received.");

        StationList stationList;
        try {
            stationList = apiYandex.getAllowStationsList();
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
                .map(settlement -> {
                    City c = new City();
                    c.setTitle(settlement.getTitle());
                    c.setApiCode(settlement.getCodes().getYandexCode());
                    return c;
                })
                .filter(city -> (StringUtils.hasText(city.getTitle()) && StringUtils.hasText(city.getApiCode())))
                .toList();

        log.info("Prepared {} cities for validation.", allCities.size());

        Set<String> existingCities = cityService.findAllCitiesWithTitleAndApiCode()
                .stream()
                .map(City::getApiCode)
                .collect(Collectors.toSet());

        log.info("Loaded {} existing cities from the database.", existingCities.size());

        List<City> newCities = allCities.stream()
                .filter(city -> !existingCities.contains(city.getApiCode()))
                .toList();

        if (!newCities.isEmpty()) {
            cityService.saveCities(newCities);
            log.info("Successfully saved {} new cities to the database.", newCities.size());
        } else {
            log.info("No new cities to save. All cities are already in the database.");
        }
    }
}
