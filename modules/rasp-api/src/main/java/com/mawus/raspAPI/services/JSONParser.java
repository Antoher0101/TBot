package com.mawus.raspAPI.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mawus.raspAPI.exceptions.HTTPClientException;
import com.mawus.raspAPI.exceptions.ParserException;

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

    public <T> T deserializeToObject(Class<T> clazz, String request, APIConnector api, Duration time) throws HTTPClientException, ParserException {
        try {
            InputStream iStream = api.getInputStream(request, time);
            return (T) objectMapper.readValue(iStream, clazz);
        } catch (IOException exceptParse) {
            throw new ParserException("Ошибка при парсинге JSON", exceptParse);
        }
    }

    public <T> String serializeToJson(T object) throws ParserException {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ParserException("Ошибка при сериализации объекта в JSON", e);
        }
    }
}
