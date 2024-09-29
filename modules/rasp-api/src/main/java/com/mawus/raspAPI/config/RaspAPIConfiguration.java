package com.mawus.raspAPI.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RaspAPIConfiguration {

    @Value("${apikey}")
    private String apiKey;

    @Value("${apilink}")
    private String apiLink;

    @Value("${api.query.limit}")
    private Integer queryLimit;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiLink() {
        return apiLink;
    }

    public void setApiLink(String apiLink) {
        this.apiLink = apiLink;
    }

    public Integer getQueryLimit() {
        return queryLimit;
    }

    public void setQueryLimit(Integer queryLimit) {
        this.queryLimit = queryLimit;
    }
}
