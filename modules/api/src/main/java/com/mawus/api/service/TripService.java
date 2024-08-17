package com.mawus.api.service;

import com.mawus.api.dto.TripDto;

import java.util.UUID;

public interface TripService {
    TripDto registerTrip(TripDto tripDto);

    TripDto getTripById(UUID id);
}
