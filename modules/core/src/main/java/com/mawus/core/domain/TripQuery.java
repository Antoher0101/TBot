package com.mawus.core.domain;


import java.time.LocalDate;

public class TripQuery {

    private String cityFromTitle;
    private String cityToTitle;
    private String transportType;
    private Integer queryOffset = 0;

    /**
     * Дата, на которую необходимо получить список рейсов.
     * Должна быть указана в формате, соответствующем стандарту ISO 8601. Например, YYYY-MM-DD.
     */
    private LocalDate date;

    public String getCityFromTitle() {
        return cityFromTitle;
    }

    public void setCityFromTitle(String cityFromTitle) {
        this.cityFromTitle = cityFromTitle;
    }

    public String getCityToTitle() {
        return cityToTitle;
    }

    public void setCityToTitle(String cityToTitle) {
        this.cityToTitle = cityToTitle;
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
}
