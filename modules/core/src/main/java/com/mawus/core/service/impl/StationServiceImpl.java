package com.mawus.core.service.impl;

import com.mawus.core.entity.City;
import com.mawus.core.entity.Station;
import com.mawus.core.repository.CityRepository;
import com.mawus.core.repository.StationRepository;
import com.mawus.core.service.StationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("bot_StationServiceImpl")
public class StationServiceImpl implements StationService {

    private final StationRepository stationRepository;
    private final CityRepository cityRepository;

    public StationServiceImpl(StationRepository stationRepository, CityRepository cityRepository) {
        this.stationRepository = stationRepository;
        this.cityRepository = cityRepository;
    }

    @Override
    public Station saveStation(Station station, City city) {
        if (city.getId() == null || !cityRepository.existsById(city.getId())) {
            city = cityRepository.save(city);
        }
        station.setCity(city);

        return stationRepository.save(station);
    }

    @Override
    public void saveStationsForCity(City city, Set<Station> stations) {
        if (city.getId() == null || !cityRepository.existsById(city.getId())) {
            city = cityRepository.save(city);
        }

        for (Station station : stations) {
            station.setCity(city);
            stationRepository.save(station);
        }
    }

    @Override
    public List<Station> getStationsForCity(City city) {
        return stationRepository.findAllByCity(city);
    }

    @Override
    public Station findStationByCode(String code) {
        return stationRepository.findByApiCode(code).orElse(null);
    }
}
