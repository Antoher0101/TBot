package com.mawus.core.domain.rasp.scheduleStation;

public enum Event {

    DEPARTURE("departure"),
    ARRIVAL("arrival");

    private String id;

    Event(String id) {
        this.id = id;
    }

    public static Event fromId(String id) {
        for (Event at : Event.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }
}
