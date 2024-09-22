package com.mawus.core.domain.rasp.scheduleStation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ScheduleStation {
    private String date;
    private Station station;
    private String event;
    private List<Schedule> schedule;
    private List<IntervalSchedule> intervalSchedule; // interval_schedule
    private ScheduleDirection ScheduleDirection; // Schedule_direction
    private Directions directions;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public List<Schedule> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<Schedule> schedule) {
        this.schedule = schedule;
    }

    public List<IntervalSchedule> getIntervalSchedule() {
        return intervalSchedule;
    }

    @JsonProperty("interval_Schedule")
    public void setIntervalSchedule(
            List<IntervalSchedule> intervalSchedule) {
        this.intervalSchedule = intervalSchedule;
    }

    public ScheduleDirection getScheduleDirection() {
        return ScheduleDirection;
    }

    @JsonProperty("Schedule_direction")
    public void setScheduleDirection(ScheduleDirection ScheduleDirection) {
        this.ScheduleDirection = ScheduleDirection;
    }

    public Directions getDirections() {
        return directions;
    }

    public void setDirections(Directions directions) {
        this.directions = directions;
    }
}
