package com.meteoservice.client;

import com.google.gson.Gson;
import com.meteoservice.dto.ForecastResponseDto;
import com.meteoservice.exception.WeatherApiException;
import com.meteoservice.model.HourlyTemperature;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class WeatherApiClient implements IWeatherApiClient {
    private final IHttpClient httpClient;
    private final String apiUrl;
    private final Gson gson;

    @Override
    public List<HourlyTemperature> getHourlyForecast(
        double latitude, 
        double longitude
    ) {
        try {
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusDays(1);

            Map<String, String> params = new HashMap<>();
            params.put("latitude", String.valueOf(latitude));
            params.put("longitude", String.valueOf(longitude));
            params.put("hourly", "temperature_2m");
            params.put("timezone", "auto");
            params.put("start_date", startDate.toString());
            params.put("end_date", endDate.toString());

            String responseBody = httpClient.get(apiUrl, params);

            ForecastResponseDto response = gson.fromJson(responseBody, ForecastResponseDto.class);
            if (response == null || response.getHourly() == null || 
                response.getHourly().getTime() == null || 
                response.getHourly().getTemperature_2m() == null) {
                throw new WeatherApiException("Invalid response format");
            }

            ForecastResponseDto.HourlyDto hourly = response.getHourly();
            List<String> times = hourly.getTime();
            List<Double> temperatures = hourly.getTemperature_2m();

            if (times.size() != temperatures.size()) {
                throw new WeatherApiException("Time and temperature arrays have different sizes");
            }

            List<HourlyTemperature> result = new ArrayList<>();
            for (int i = 0; i < times.size(); i++) {
                result.add(HourlyTemperature.builder()
                    .hour(times.get(i))
                    .temperature(temperatures.get(i))
                    .build());
            }

            System.out.println("Weather forecast fetched: " + result.size() + " hours (unfiltered)");

            return result;

        } catch (IOException e) {
            throw new WeatherApiException("Failed to fetch weather data: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new WeatherApiException("Unexpected error: " + e.getMessage(), e);
        }
    }
}

