package com.mawus.raspAPI.services;

public class RaspQueryParams {

    private final String to;
    private final String from;
    private final String date;
    private final String transportType;
    private final String limit;
    private final String offset;
    private final String resultTimezone;
    private final String withTransfers;
    private final String station;
    private final String direction;
    private final String event;
    private final String uid;
    private final String latitude;
    private final String longitude;
    private final String distance;
    private final String code;
    private final String stationType;

    private RaspQueryParams(Builder builder) {
        this.to = builder.to;
        this.from = builder.from;
        this.date = builder.date;
        this.transportType = builder.transportType;
        this.limit = builder.limit;
        this.offset = builder.offset;
        this.resultTimezone = builder.resultTimezone;
        this.withTransfers = builder.withTransfers;
        this.station = builder.station;
        this.direction = builder.direction;
        this.event = builder.event;
        this.uid = builder.uid;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.distance = builder.distance;
        this.code = builder.code;
        this.stationType = builder.stationType;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getDate() {
        return date;
    }

    public String getTransportType() {
        return transportType;
    }

    public String getLimit() {
        return limit;
    }

    public String getResultTimezone() {
        return resultTimezone;
    }

    public String getWithTransfers() {
        return withTransfers;
    }

    public String getStation() {
        return station;
    }

    public String getDirection() {
        return direction;
    }

    public String getEvent() {
        return event;
    }

    public String getOffset() {
        return offset;
    }

    public String getUid() {
        return uid;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getDistance() {
        return distance;
    }

    public String getCode() {
        return code;
    }

    public String getStationType() {
        return stationType;
    }

    public static class Builder {
        private String to = "";
        private String from = "";
        private String date = "";
        private String transportType = "";
        private String limit = "";
        private String offset = "";
        private String resultTimezone = "";
        private String withTransfers = "";
        private String station = "";
        private String direction = "";
        private String event = "";
        private String uid = "";
        private String latitude = "";
        private String longitude = "";
        private String distance = "";
        private String code = "";
        private String stationType = "";

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public Builder transportType(String transportType) {
            this.transportType = transportType;
            return this;
        }

        public Builder limit(String limit) {
            this.limit = limit;
            return this;
        }

        public Builder offset(String offset) {
            this.offset = offset;
            return this;
        }

        public Builder resultTimezone(String resultTimezone) {
            this.resultTimezone = resultTimezone;
            return this;
        }

        public Builder withTransfers(String withTransfers) {
            this.withTransfers = withTransfers;
            return this;
        }

        public Builder station(String station) {
            this.station = station;
            return this;
        }

        public Builder direction(String direction) {
            this.direction = direction;
            return this;
        }

        public Builder event(String event) {
            this.event = event;
            return this;
        }

        public Builder uid(String uid) {
            this.uid = uid;
            return this;
        }

        public Builder latitude(String latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(String longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder distance(String distance) {
            this.distance = distance;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder stationType(String stationType) {
            this.stationType = stationType;
            return this;
        }

        public RaspQueryParams build() {
            return new RaspQueryParams(this);
        }
    }
}