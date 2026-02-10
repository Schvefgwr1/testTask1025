package com.meteoservice.exception;

import com.common.core.exception.ApplicationException;

/**
 * Исключение при ошибке геокодинга
 */
public class GeocodingException extends ApplicationException {
    public GeocodingException(String message) {
        super("Geocoding error: " + message, 502);
    }

    public GeocodingException(String message, Throwable cause) {
        super("Geocoding error: " + message, 502, cause);
    }
}

