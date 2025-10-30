package com.meteoservice.handler;

import com.google.gson.Gson;
import com.meteoservice.core.http.PrimaryHandler;
import com.meteoservice.core.http.ResponseHelper;
import com.meteoservice.exception.ValidationException;
import com.meteoservice.model.HourlyTemperature;
import com.meteoservice.model.WeatherData;
import com.meteoservice.service.IWeatherService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
        super(gson, responseHelper);
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

