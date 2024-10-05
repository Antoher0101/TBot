package com.mawus.raspAPI.services;

import java.util.ArrayList;

public class RequestBuilder {

    private final String url;
    private String branch;
    private String request;

    public RequestBuilder(String curl) {
        url = curl;
    }

    public void setBranch(String cBranch) {
        branch = cBranch;
        request = url + branch;
    }

    public String addParams(RaspQueryParams param) {

        ArrayList<String> params = new ArrayList<>();

        if (!param.getTo().isEmpty()) {
            params.add("to=" + param.getTo());
        }
        if (!param.getFrom().isEmpty()) {
            params.add("from=" + param.getFrom());
        }
        if (!param.getDate().isEmpty()) {
            params.add("date=" + param.getDate());
        }
        if (!param.getTransportType().isEmpty()) {
            params.add("transport_types=" + param.getTransportType());
        }
        if (!param.getLimit().isEmpty()) {
            params.add("limit=" + param.getLimit());
        }
        if (!param.getLimit().isEmpty()) {
            params.add("offset=" + param.getOffset());
        }
        if (!param.getResultTimezone().isEmpty()) {
            params.add("result_timezone=" + param.getResultTimezone());
        }
        if (!param.getWithTransfers().isEmpty()) {
            params.add("transfers=" + param.getWithTransfers());
        }
        if (!param.getStation().isEmpty()) {
            params.add("station=" + param.getStation());
        }
        if (!param.getDirection().isEmpty()) {
            params.add("direction=" + param.getDirection());
        }
        if (!param.getEvent().isEmpty()) {
            params.add("event=" + param.getEvent());
        }
        if (!param.getUid().isEmpty()) {
            params.add("uid=" + param.getUid());
        }
        if (!param.getLatitude().isEmpty()) {
            params.add("lat=" + param.getLatitude());
        }
        if (!param.getLongitude().isEmpty()) {
            params.add("lng=" + param.getLongitude());
        }
        if (!param.getDistance().isEmpty()) {
            params.add("distance=" + param.getDistance());
        }
        if (!param.getCode().isEmpty()) {
            params.add("code=" + param.getCode());
        }
        if (!param.getStationType().isEmpty()) {
            params.add("station_types=" + param.getStationType());
        }

        request += params.get(0);
        for (int idx = 1; idx < params.size(); ++idx) {
            request += "&" + params.get(idx);
        }
        return request;
    }

    public String getRequest() {
        return request;
    }
}
