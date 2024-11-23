package com.mawus.core.jmx.api;

import com.mawus.core.events.CityUpdateEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@Component
@ManagedResource(objectName = "com.mawus.core.jmx.api:type=CityAPI", description = "City API Management Bean")
public class CityAPI implements CityAPIMbean {

    private final ApplicationEventPublisher eventPublisher;

    public CityAPI(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @ManagedOperation(description = "Send update event to update cities from API")
    @Override
    public void sendUpdateCityEvent() {
        eventPublisher.publishEvent(new CityUpdateEvent(this));
    }
}
