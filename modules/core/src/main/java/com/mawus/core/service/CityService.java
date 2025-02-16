package com.mawus.core.service;

import com.mawus.core.entity.City;

import java.util.Collection;
import java.util.List;

public interface CityService {
    void saveCity(City city);

    void saveCities(List<City> cities);

    Collection<City> findAll();

    City findByTitle(String title);

    City findByTitleElastic(String title);

    City findByCode(String code);

    void saveCityWithStations(List<City> city);

    City findCityByStationCode(String stationCode);
}

