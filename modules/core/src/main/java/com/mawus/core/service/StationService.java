package com.mawus.core.service;

import com.mawus.core.entity.City;
import com.mawus.core.entity.Station;

import java.util.List;
import java.util.Set;

public interface StationService {

    Station saveStation(Station station, City city);
    void saveStationsForCity(City city, Set<Station> stations);
    List<Station> getStationsForCity(City city);
    Station findStationByCode(String code);
}
