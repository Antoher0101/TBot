package com.mawus.core.events;

import org.springframework.context.ApplicationEvent;

public class CityUpdateEvent extends ApplicationEvent {

    public CityUpdateEvent(Object source) {
        super(source);
    }
}
