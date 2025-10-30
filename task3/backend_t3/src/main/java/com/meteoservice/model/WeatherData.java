package com.meteoservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Полные данные о погоде для города
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {
    private String city;
    private Coordinates coordinates;
    private List<HourlyTemperature> hourlyData;
    private LocalDateTime cachedAt;
    private LocalDateTime validFrom;    // С какого часа данные валидны
    private LocalDateTime validUntil;   // До какого часа данные валидны (не включительно)
}

