package com.mawus.raspAPI.listeners;

import com.mawus.core.domain.rasp.stationList.StationList;
import com.mawus.core.entity.City;
import com.mawus.core.events.CityUpdateScheduleEvent;
import com.mawus.core.service.CityService;
import com.mawus.raspAPI.api.APIYandex;
import com.mawus.raspAPI.exceptions.HTTPClientException;
import com.mawus.raspAPI.exceptions.ParserException;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CityUpdateEventListener implements ApplicationListener<CityUpdateScheduleEvent> {

    private final APIYandex apiYandex;
    private final CityService cityService;

    public CityUpdateEventListener(APIYandex apiYandex, CityService cityService) {
        this.apiYandex = apiYandex;
        this.cityService = cityService;
    }

    @Override
    public void onApplicationEvent(CityUpdateScheduleEvent event) {
        StationList stationList;
        try {
            stationList = apiYandex.getAllowStationsList();
        } catch (HTTPClientException e) {
            throw new RuntimeException(e);
        } catch (ParserException e) {
            throw new RuntimeException(e);
        }

        List<City> cities = stationList.getCountries().stream()
                .filter(c -> c.getTitle().equals("Россия"))
                .flatMap(country -> country.getRegions().stream())
                .flatMap(region -> region.getSettlements().stream())
                .map(settlement -> {
                    City c = new City();
                    c.setName(settlement.getTitle());
                    c.setApiCode(settlement.getCodes().getYandexCode());
                    return c;
                })
                .filter(city -> city.getName() != null)
                .toList();

        cityService.saveCities(cities);
    }
}
