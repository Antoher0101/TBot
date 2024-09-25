package com.mawus.core.service.impl;

import com.mawus.core.events.CityUpdateScheduleEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

    private final ApplicationEventPublisher eventPublisher;

    public ScheduleService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(cron = "0 0 4 * * SAT")
    public void triggerFetchSchedule() {
        eventPublisher.publishEvent(new CityUpdateScheduleEvent(this));
    }
}
