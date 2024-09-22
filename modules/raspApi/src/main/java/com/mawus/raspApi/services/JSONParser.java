package com.mawus.raspApi.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mawus.raspApi.exceptions.HTTPClientException;
import com.mawus.raspApi.exceptions.ParserException;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

public class JSONParser {

    private final ObjectMapper objectMapper;
    boolean failUnknownProperties = false;

    public JSONParser(boolean failUnknownProperties) {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failUnknownProperties);
        this.failUnknownProperties = failUnknownProperties;
    }

    public JSONParser() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failUnknownProperties);
    }

    public <T> T parseIntoObject(Class<T> clazz, String request, APIConnector api, Duration time) throws HTTPClientException, ParserException {
        try {
            InputStream iStream = api.getInputStream(request, time);
            return (T) objectMapper.readValue(iStream, clazz);
        } catch (IOException exceptParse) {
            throw new ParserException("Ошибка при парсинге JSON", exceptParse);
        }
    }
}
