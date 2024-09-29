package com.mawus.core.repository;

import com.mawus.core.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CityRepository extends JpaRepository<City, UUID> {
    Optional<City> findByTitle(String title);

    Optional<City> findByApiCode(String code);
}
