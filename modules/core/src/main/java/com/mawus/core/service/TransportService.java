package com.mawus.core.service;

import com.mawus.core.entity.Transport;
import com.mawus.core.entity.TransportType;

import java.util.List;

public interface TransportService {

    List<Transport> findAll();

    List<TransportType> findAllTypes();

    TransportType findByName(String name);
}
