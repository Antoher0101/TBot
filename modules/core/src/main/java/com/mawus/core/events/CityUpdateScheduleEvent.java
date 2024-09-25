package com.mawus.core.events;

import org.springframework.context.ApplicationEvent;

public class CityUpdateScheduleEvent extends ApplicationEvent {

    public CityUpdateScheduleEvent(Object source) {
        super(source);
    }
}
