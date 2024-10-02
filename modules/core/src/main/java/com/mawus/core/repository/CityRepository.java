package com.mawus.core.repository;

import com.mawus.core.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CityRepository extends JpaRepository<City, UUID> {
    Optional<City> findByTitle(String title);

    Optional<City> findByApiCode(String code);

    @Query("select c from bot$City c join c.stations s where s.apiCode = :apiCode")
    City findCityByStationApiCode(@Param("apiCode") String apiCode);
}
