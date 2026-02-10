package com.meteoservice.handler;

import com.google.gson.Gson;
import com.common.core.http.MultipartParser;
import com.common.core.http.PrimaryHandler;
import com.common.core.http.ResponseHelper;
import com.common.core.exception.ValidationException;
import com.meteoservice.model.WeatherData;
import com.meteoservice.service.IWeatherService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Handler для обработки запросов погоды
 */
public class WeatherHandler extends PrimaryHandler {
    private final IWeatherService weatherService;

    public WeatherHandler(
        IWeatherService weatherService,
        Gson gson,
        ResponseHelper responseHelper
    ) {
        super(gson, responseHelper, new MultipartParser());
        this.weatherService = weatherService;
    }

    /**
     * Обрабатывает GET /weather?city={city}
     */
    public void handleGetWeather(HttpExchange exchange) throws IOException {
        String city = getQueryParam(exchange, "city");

        if (city == null || city.trim().isEmpty()) {
            throw new ValidationException("City parameter must be required");
        }

        WeatherData weatherData = weatherService.getWeatherForCity(city);
        sendJsonResponse(exchange, 200, weatherData);
    }
}

