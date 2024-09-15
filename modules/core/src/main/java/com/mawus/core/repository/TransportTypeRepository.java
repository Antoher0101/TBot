package com.mawus.core.repository;

import com.mawus.core.entity.TransportType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransportTypeRepository extends JpaRepository<TransportType, UUID> {
    Optional<TransportType> findByName(String name);
}
