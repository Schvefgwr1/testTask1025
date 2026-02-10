package com.meteoservice.service;

import com.meteoservice.client.IGeocodingClient;
import com.meteoservice.client.IWeatherApiClient;
import com.common.core.cache.ICache;
import com.meteoservice.model.Coordinates;
import com.meteoservice.model.HourlyTemperature;
import com.meteoservice.model.WeatherData;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class WeatherService implements IWeatherService {
    private final IGeocodingClient geocodingClient;
    private final IWeatherApiClient weatherApiClient;
    private final ICache<String> cache;
    private final int cacheTimeoutSeconds;

    @Override
    public WeatherData getWeatherForCity(String city) {
        System.out.println("\n=== Processing weather request for city: " + city + " ===");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentHour = now.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime requiredUntil = currentHour.plusHours(24);

        String normalizedCity = city.trim().toLowerCase();
        String cacheKey = "weather:" + normalizedCity;
        Optional<WeatherData> cachedData = cache.get(cacheKey, WeatherData.class);

        if (cachedData.isPresent()) {
            WeatherData cached = cachedData.get();

            if (!cached.getValidFrom().isAfter(currentHour) && !cached.getValidUntil().isBefore(requiredUntil)) {
                return filterHourlyData(cached, currentHour, requiredUntil);
            }

            WeatherData refreshed = fetchAndCache48(city, currentHour);
            return filterHourlyData(refreshed, currentHour, requiredUntil);
        }

        WeatherData initial = fetchAndCache48(city, currentHour);
        return filterHourlyData(initial, currentHour, requiredUntil);
    }

    /**
     * Фильтрует данные из кэша, оставляя только нужный диапазон
     */
    private WeatherData filterHourlyData(
        WeatherData cached,
        LocalDateTime from,
        LocalDateTime until
    ) {
        List<HourlyTemperature> filtered = cached.getHourlyData().stream()
            .filter(h -> {
                LocalDateTime hourTime = LocalDateTime.parse(h.getHour());
                return !hourTime.isBefore(from) && hourTime.isBefore(until);
            })
            .collect(Collectors.toList());

        return WeatherData.builder()
            .city(cached.getCity())
            .coordinates(cached.getCoordinates())
            .hourlyData(filtered)
            .cachedAt(cached.getCachedAt())
            .validFrom(from)
            .validUntil(until)
            .build();
    }

    /**
     * Запрашивает данные из API и кэширует их
     */
    private WeatherData fetchAndCache48(String city, LocalDateTime currentHour) {
        Coordinates coords = geocodingClient.getCoordinates(city);

        List<HourlyTemperature> raw = weatherApiClient.getHourlyForecast(
            coords.getLatitude(),
            coords.getLongitude()
        );

        WeatherData weatherData = WeatherData.builder()
            .city(city)
            .coordinates(coords)
            .hourlyData(raw)
            .cachedAt(LocalDateTime.now())
            .validFrom(currentHour)
            .validUntil(currentHour.plusHours(48))
            .build();

        try {
            cache.set(cacheKey(city), weatherData, cacheTimeoutSeconds);
        } catch (Exception e) {
            System.err.println("Failed to cache weather data: " + e.getMessage());
        }

        return weatherData;
    }

    /**
     * Формирует ключ кэша для города
     */
    private String cacheKey(String city) {
        return "weather:" + city.trim().toLowerCase();
    }
}

