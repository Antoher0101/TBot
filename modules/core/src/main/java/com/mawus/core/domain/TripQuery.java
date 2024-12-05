package com.mawus.core.domain;


import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class TripQuery implements Serializable {
    @Serial
    private static final long serialVersionUID = -5093281332357760863L;

    private String stationFromCode;
    private String stationToCode;
    private String transportType;
    private Integer queryOffset = 0;

    /**
     * Дата, на которую необходимо получить список рейсов.
     * Должна быть указана в формате, соответствующем стандарту ISO 8601. Например, YYYY-MM-DD.
     */
    private LocalDate date;

    public String getStationFromCode() {
        return stationFromCode;
    }

    public void setStationFromCode(String stationFromCode) {
        this.stationFromCode = stationFromCode;
    }

    public String getStationToCode() {
        return stationToCode;
    }

    public void setStationToCode(String stationToCode) {
        this.stationToCode = stationToCode;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public Integer getQueryOffset() {
        return queryOffset;
    }

    public void setQueryOffset(Integer queryOffset) {
        this.queryOffset = queryOffset;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String toString() {
        return "TripQuery{" +
               "cityFromTitle='" + stationFromCode + '\'' +
               ", cityToTitle='" + stationToCode + '\'' +
               ", transportType='" + transportType + '\'' +
               ", queryOffset=" + queryOffset +
               ", date=" + date +
               '}';
    }
}
