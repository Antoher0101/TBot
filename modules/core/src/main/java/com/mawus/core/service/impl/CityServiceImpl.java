package com.mawus.core.service.impl;

import com.mawus.core.entity.City;
import com.mawus.core.repository.CityRepository;
import com.mawus.core.service.CityService;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
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
    public City findByCode(String code) {
        return cityRepository.findByApiCode(code).orElse(null);
    }
}
