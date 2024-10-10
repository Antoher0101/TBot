package com.mawus.core.repository;

import com.mawus.core.entity.Trip;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {
    List<Trip> findByClientId(UUID id);

    @Query("select b from bot$Trip b where b.client.id = ?1")
    List<Trip> findByClient_Id(UUID id, Pageable pageable);

    long countByClient_Id(UUID id);
}
