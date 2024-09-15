package com.mawus.core.service.impl;

import com.mawus.core.entity.Transport;
import com.mawus.core.entity.TransportType;
import com.mawus.core.repository.TransportRepository;
import com.mawus.core.repository.TransportTypeRepository;
import com.mawus.core.service.TransportService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportServiceImpl implements TransportService {

    private final TransportRepository transportRepository;
    private final TransportTypeRepository transportTypeRepository;

    public TransportServiceImpl(TransportRepository transportRepository, TransportTypeRepository transportTypeRepository) {
        this.transportRepository = transportRepository;
        this.transportTypeRepository = transportTypeRepository;
    }

    @Override
    public List<Transport> findAll() {
        return transportRepository.findAll();
    }

    @Override
    public List<TransportType> findAllTypes() {
        return transportTypeRepository.findAll();
    }

    @Override
    public TransportType findByName(String name) {
        return transportTypeRepository.findByName(name).orElse(null);
    }
}
