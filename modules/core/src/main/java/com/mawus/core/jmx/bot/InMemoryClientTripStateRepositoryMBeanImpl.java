package com.mawus.core.jmx.bot;

import com.mawus.core.repository.nonpersistent.impl.InMemoryClientTripStateRepository;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@ManagedResource(
        objectName = "com.mawus.bot:type=InMemoryClientTripStateRepository",
        description = "Manage InMemoryClientTripStateRepository via JMX"
)
public class InMemoryClientTripStateRepositoryMBeanImpl implements InMemoryClientTripStateRepositoryMBean {

    private InMemoryClientTripStateRepository repository;

    public InMemoryClientTripStateRepositoryMBeanImpl(InMemoryClientTripStateRepository repository) {
        this.repository = repository;
    }

    @Override
    @ManagedOperation(description = "Get all client trips as a formatted string")
    public String getAllTrips() {
        return repository.getClientsTrip()
                .entrySet()
                .stream()
                .map(entry -> "ClientId: " + entry.getKey() + ", Trip: " + entry.getValue().toString())
                .collect(Collectors.joining("\n", "All Trips:\n", ""));
    }

    @Override
    @ManagedOperation(description = "Get trip details for a specific client")
    public String getTrip(Long clientId) {
        var trip = repository.getTrip(clientId);
        return trip != null
                ? "ClientId: " + clientId + ", Trip: " + trip
                : "No trip found for ClientId: " + clientId;
    }

    @Override
    @ManagedOperation(description = "Remove a trip for a specific client")
    public String removeTrip(Long clientId) {
        if (repository.getTrip(clientId) == null) {
            return "No trip found for ClientId: " + clientId;
        }
        repository.disposeDraftTrip(clientId);
        return "Removed trip for ClientId: " + clientId;
    }

    @Override
    @ManagedAttribute(description = "Get the total count of client trips")
    public String getTripCount() {
        return "Total trips: " + repository.getClientsTrip().size();
    }
}
