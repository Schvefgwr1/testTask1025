package com.meteoservice.client;

import com.google.gson.Gson;
import com.meteoservice.dto.GeocodingResponseDto;
import com.meteoservice.exception.CityNotFoundException;
import com.meteoservice.exception.GeocodingException;
import com.meteoservice.model.Coordinates;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.Map;

@AllArgsConstructor
public class GeocodingClient implements IGeocodingClient {
    private final IHttpClient httpClient;
    private final String apiUrl;
    private final Gson gson;

    @Override
    public Coordinates getCoordinates(String city) {
        try {
            // Формируем запрос к Geocoding API
            Map<String, String> params = Map.of(
                "name", city,
                "count", "1",
                "language", "en",
                "format", "json"
            );

            String responseBody = httpClient.get(apiUrl, params);

            // Парсим ответ
            GeocodingResponseDto response = gson.fromJson(responseBody, GeocodingResponseDto.class);

            // Проверяем, что результаты есть
            if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
                throw new CityNotFoundException(city);
            }

            GeocodingResponseDto.LocationDto location = response.getResults().get(0);

            System.out.println("Coordinates found: lat=" + location.getLatitude() + 
                             ", lon=" + location.getLongitude() + 
                             " (Country: " + location.getCountry() + ")");

            return Coordinates.builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();

        } catch (CityNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw new GeocodingException("Failed to fetch coordinates: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new GeocodingException("Unexpected error: " + e.getMessage(), e);
        }
    }
}

