package com.meteoservice.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO для ответа от Geocoding API
 */
@Data
public class GeocodingResponseDto {
    private List<LocationDto> results;

    @Data
    public static class LocationDto {
        private long id;
        private String name;
        private double latitude;
        private double longitude;
        private String country;
        private String timezone;
    }
}

