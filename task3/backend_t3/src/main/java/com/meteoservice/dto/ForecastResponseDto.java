package com.meteoservice.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO для ответа от Weather API
 */
@Data
public class ForecastResponseDto {
    private double latitude;
    private double longitude;
    private HourlyDto hourly;

    @Data
    public static class HourlyDto {
        private List<String> time;
        private List<Double> temperature_2m;
    }
}

