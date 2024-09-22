package com.mawus.raspApi.services;

import com.mawus.raspApi.exceptions.FieldHolder;
import com.mawus.raspApi.exceptions.HTTPClientException;
import com.mawus.raspApi.exceptions.ValidationException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class APIConnector {

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
                .headers("Authorization", APIKey).build();

        if (APIKey.isEmpty()) {
            throw new HTTPClientException("Поле ключа пустое");
        }

        HttpResponse<InputStream> response;

        try {
            response = client.send(httpReq, HttpResponse.BodyHandlers.ofInputStream());
        } catch (IOException except) {
            throw new HTTPClientException("Ошибка при работе HTTP клиента", except);
        } catch (InterruptedException except) {
            Thread.currentThread().interrupt();
            throw new HTTPClientException("Ошибка при работе с потоками HTTP клиента", except);
        }

        if (response.statusCode() == HttpStatus.OK.value()) {
            return response.body();
        }

        throw new HTTPClientException("Код ответа - " + response.statusCode());
    }
}
