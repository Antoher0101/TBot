package com.mawus.core.app.service;

import com.mawus.core.events.CityUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service("core_ScheduleService")
public class ScheduleService {
    private final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    private final ApplicationEventPublisher eventPublisher;

    public ScheduleService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(cron = "${core.scheduler.update-city.cron}")
    public void CityUpdateSchedule() {
        log.info("The scheduled task of retrieving cities from the API has begun now.");
        eventPublisher.publishEvent(new CityUpdateEvent(this));
    }
}
