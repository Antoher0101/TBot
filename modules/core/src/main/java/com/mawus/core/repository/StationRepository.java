package com.mawus.core.repository;

import com.mawus.core.entity.City;
import com.mawus.core.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StationRepository extends JpaRepository<Station, UUID> {
    List<Station> findAllByCity(City city);

    Optional<Station> findByApiCode(String apiCode);
}
