package com.mawus.core;

import com.mawus.core.entity.City;
import com.mawus.core.entity.Station;
import com.mawus.core.repository.CityRepository;
import com.mawus.core.repository.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = {CoreApplicationTest.class})
@ActiveProfiles("test")
public class CityStationRepositoryTest  {

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private StationRepository stationRepository;
    @Test
    public void testFindByApiCode() {
        City city = new City();
        city.setTitle("Москва");
        city.setApiCode("MSK");
        cityRepository.save(city);

        Optional<City> foundCity = cityRepository.findByApiCode("MSK");

        assertTrue(foundCity.isPresent());
        assertEquals("Москва", foundCity.get().getTitle());
    }

    @Test
    public void testSaveCityAndStation() {
        City city = new City();
        city.setTitle("Moscow");
        city.setApiCode("MSK");

        City savedCity = cityRepository.save(city);
        assertNotNull(savedCity.getId());

        Station station = new Station();
        station.setName("Moscow Station 1");
        station.setApiCode("MSK1");
        station.setCity(savedCity);

        Station savedStation = stationRepository.save(station);
        assertNotNull(savedStation.getId());
        assertEquals(savedCity.getId(), savedStation.getCity().getId());

        List<Station> stations = stationRepository.findAllByCity(savedCity);
        assertEquals(1, stations.size());
        assertEquals("Moscow Station 1", stations.get(0).getName());
    }

    @Test
    public void testFindCityByTitle() {
        City city = new City();
        city.setTitle("Saint Petersburg");
        city.setApiCode("SPB");

        cityRepository.save(city);

        Optional<City> foundCity = cityRepository.findByTitle("Saint Petersburg");
        assertTrue(foundCity.isPresent());
        assertEquals("SPB", foundCity.get().getApiCode());
    }

    @Test
    public void testDeleteCityWithoutCascade() {
        City city = new City();
        city.setTitle("Kazan");
        city.setApiCode("KZN");

        City savedCity = cityRepository.save(city);

        Station station = new Station();
        station.setName("Kazan Station 1");
        station.setApiCode("KZN1");
        station.setCity(savedCity);

        stationRepository.save(station);

        cityRepository.delete(savedCity);

        Optional<Station> foundStation = stationRepository.findById(station.getId());
        assertTrue(foundStation.isPresent());
        assertEquals("Kazan Station 1", foundStation.get().getName());
    }
}