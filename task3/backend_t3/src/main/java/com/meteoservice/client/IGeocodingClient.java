package com.meteoservice.client;

import com.meteoservice.exception.CityNotFoundException;
import com.meteoservice.exception.GeocodingException;
import com.meteoservice.model.Coordinates;

/**
 * Интерфейс клиента для работы с Geocoding API
 */
public interface IGeocodingClient {
    /**
     * Получает координаты города по названию
     * 
     * @param city название города
     * @return координаты города
     * @throws CityNotFoundException если город не найден
     * @throws GeocodingException при ошибке API
     */
    Coordinates getCoordinates(String city);
}

