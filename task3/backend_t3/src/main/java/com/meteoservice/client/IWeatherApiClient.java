package com.meteoservice.client;

import com.meteoservice.exception.WeatherApiException;
import com.meteoservice.model.HourlyTemperature;

import java.util.List;

/**
 * Интерфейс клиента для работы с Weather API
 */
public interface IWeatherApiClient {
    /**
     * Получает почасовой прогноз погоды
     * 
     * @param latitude широта
     * @param longitude долгота
     * @return список температур по часам
     * @throws WeatherApiException при ошибке API
     */
    List<HourlyTemperature> getHourlyForecast(
        double latitude, 
        double longitude
    );
}

