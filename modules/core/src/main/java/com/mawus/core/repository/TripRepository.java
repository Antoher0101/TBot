package com.mawus.core.repository;

import com.mawus.core.entity.Station;
import com.mawus.core.entity.Trip;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {
    List<Trip> findByClientId(UUID id);

    @Query("select b from bot$Trip b where b.client.id = ?1")
    List<Trip> findByClient_Id(UUID id, Pageable pageable);

    long countByClient_Id(UUID id);

    @EntityGraph(attributePaths = {"client.user", "transport.transportType", "stationFrom.city", "stationTo.city"})
    @Query("SELECT t FROM bot$Trip t WHERE t.tripNumber = :#{#currentTrip.tripNumber} " +
           "and exists (select 1 from t.intermediateStations s where s in :#{#currentTrip.intermediateStations}) " +
           "and t.client.user.id <> :#{#currentTrip.client.user.id} " +
           "AND (t.departureTime <= :#{#currentTrip.arrivalTime} " +
           "AND t.arrivalTime >= :#{#currentTrip.departureTime})")
    List<Trip> findCompanionTrips(@Param("currentTrip") Trip currentTrip);

    @EntityGraph(attributePaths = {"client.user", "transport.transportType", "stationFrom.city", "stationTo.city"})
    @Query("SELECT t FROM bot$Trip t WHERE t.tripNumber = :#{#currentTrip.tripNumber} " +
           "and exists (select 1 from t.intermediateStations s where s in :#{#currentTrip.intermediateStations}) " +
           "and t.client.user.id <> :#{#currentTrip.client.user.id} " +
           "AND (t.departureTime <= :#{#currentTrip.arrivalTime} " +
           "AND t.arrivalTime >= :#{#currentTrip.departureTime})")
    List<Trip> findPageableCompanionTrips(@Param("currentTrip") Trip currentTrip, Pageable pageable);

    @EntityGraph(attributePaths = {"stationFrom.city", "stationTo.city", "intermediateStations.city"})
    List<Trip> findFullTripsByClient_Id(UUID id);
}
