package com.meteoservice.exception;

/**
 * Исключение, когда город не найден в геокодинге
 */
public class CityNotFoundException extends ApplicationException {
    public CityNotFoundException(String city) {
        super("City not found: " + city, 404);
    }

    public CityNotFoundException(String city, Throwable cause) {
        super("City not found: " + city, 404, cause);
    }
}

