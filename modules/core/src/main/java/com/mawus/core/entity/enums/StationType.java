package com.mawus.core.entity.enums;

public enum StationType {
    STATION("station"),                     //станция
    PLATFORM("platform"),                   // платформа
    STOP("stop"),                           // остановочный пункт
    CHECKPOINT("checkpoint"),               // блок-пост
    POST("post"),                           // пост
    CROSSING("crossing"),                   // разъезд
    OVERTAKING_POINT("overtaking_point"),   // обгонный пункт
    TRAIN_STATION("train_station"),         // вокзал
    AIRPORT("airport"),                     // аэропорт
    BUS_STATION("bus_station"),             // автовокзал
    BUT_STOP("bus_stop"),                   // автобусная остановка
    UNKNOWN("unknown"),                     // станция без типа
    PORT("port"),                           // порт
    PORT_POINT("port_point"),               // порт-пункт
    WHARF("wharf"),                         // пристань
    RIVER_PORT("river_port"),               // речной вокзал
    MARINE_STATION("marine_station");       // морской вокзал

    private String id;

    StationType(String id) {
        this.id = id;
    }

    public static StationType fromId(String id) {
        for (StationType at : StationType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return UNKNOWN;
    }

    public String getId() {
        return id;
    }
}
