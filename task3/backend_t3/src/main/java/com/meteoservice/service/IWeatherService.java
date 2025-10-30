package com.meteoservice.service;

import com.meteoservice.exception.CityNotFoundException;
import com.meteoservice.exception.WeatherApiException;
import com.meteoservice.model.WeatherData;

/**
 * Интерфейс сервиса для получения данных о погоде
 */
public interface IWeatherService {
    /**
     * Получает данные о погоде для указанного города
     * 
     * @param city название города
     * @return данные о погоде
     * @throws CityNotFoundException если город не найден
     * @throws WeatherApiException при ошибке получения данных о погоде
     */
    WeatherData getWeatherForCity(String city);
}

