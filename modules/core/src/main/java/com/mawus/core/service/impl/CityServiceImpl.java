package com.mawus.core.service.impl;

import com.mawus.core.entity.City;
import com.mawus.core.entity.Station;
import com.mawus.core.repository.CityRepository;
import com.mawus.core.repository.StationRepository;
import com.mawus.core.service.CityService;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;
    private final StationRepository stationRepository;

    public CityServiceImpl(CityRepository cityRepository, StationRepository stationRepository) {
        this.cityRepository = cityRepository;
        this.stationRepository = stationRepository;
    }

    @Override
    public void saveCity(City city) {
        cityRepository.save(city);
    }

    @Override
    public void saveCities(List<City> cities) {
        cityRepository.saveAll(cities);
    }

    @Override
    public Collection<City> findAll() {
        return cityRepository.findAll();
    }

    @Nullable
    @Override
    public City findByTitle(String title) {
        return cityRepository.findByTitle(title).orElse(null);
    }

    @Override
    public City findByTitleElastic(String title) {
        return cityRepository.findByTitleElastic(title).orElse(null);
    }

    @Override
    public City findByCode(String code) {
        return cityRepository.findByApiCode(code).orElse(null);
    }

    @Override
    public void saveCityWithStations(List<City> cities) {
        for (City c : cities) {
            if (c.getId() == null || !cityRepository.existsById(c.getId())) {
                c = cityRepository.save(c);
            }

            for (Station station : c.getStations()) {
                station.setCity(c);
                stationRepository.save(station);
            }
        }
    }

    @Override
    public City findCityByStationCode(String stationCode) {
        return cityRepository.findCityByStationApiCode(stationCode);
    }
}
