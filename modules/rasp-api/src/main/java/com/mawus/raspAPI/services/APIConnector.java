package com.mawus.raspAPI.services;

import com.mawus.raspAPI.exceptions.FieldHolder;
import com.mawus.raspAPI.exceptions.HTTPClientException;
import com.mawus.raspAPI.exceptions.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class APIConnector {
    private final Logger log = LoggerFactory.getLogger(APIConnector.class);

    private final String APIKey;

    public APIConnector(String key) throws ValidationException {

        if (key.isEmpty()) {
            FieldHolder errorKey = new FieldHolder("APIKey", key);
            List<FieldHolder> holdersField = new ArrayList<>();
            holdersField.add(errorKey);
            throw new ValidationException("Ошибка валидации ключа", holdersField);
        }

        APIKey = key;
    }

    public InputStream getInputStream(String request, Duration time) throws HTTPClientException {
        HttpClient client = HttpClient.newBuilder().connectTimeout(time).build();
        HttpRequest httpReq = HttpRequest.newBuilder(URI.create(request))
                .headers("Authorization", APIKey)
                .build();

        if (APIKey == null || APIKey.isEmpty()) {
            log.error("HTTP request failed: API key is empty");
            throw new HTTPClientException("Поле ключа пустое");
        }

        HttpResponse<InputStream> response;

        try {
            log.debug("Sending HTTP request to: {}", request);
            log.debug("Request Headers: Authorization=[PROTECTED]");
            log.debug("Request Timeout: {} ms", time.toMillis());

            response = client.send(httpReq, HttpResponse.BodyHandlers.ofInputStream());
        } catch (IOException except) {
            log.error("IOException occurred while sending HTTP request to {}: {}", request, except.getMessage(), except);
            throw new HTTPClientException("Ошибка при работе HTTP клиента", except);
        } catch (InterruptedException except) {
            log.error("InterruptedException occurred while sending HTTP request to {}: {}", request, except.getMessage(), except);
            Thread.currentThread().interrupt();
            throw new HTTPClientException("Ошибка при работе с потоками HTTP клиента", except);
        }

        log.debug("HTTP Response Code: {}", response.statusCode());

        if (response.statusCode() == HttpStatus.OK.value()) {
            return response.body();
        } else {
            log.error("HTTP request failed: Response Code={}, URI={}", response.statusCode(), request);
            if (response.body() != null) {
                try (InputStream errorStream = response.body()) {
                    String errorResponse = new BufferedReader(new InputStreamReader(errorStream, StandardCharsets.UTF_8))
                            .lines().collect(Collectors.joining("\n"));
                    log.error("Response Body: {}", errorResponse);
                } catch (IOException e) {
                    log.error("Failed to log response body", e);
                }
            }
        }

        throw new HTTPClientException("Код ответа - " + response.statusCode());
    }
}
