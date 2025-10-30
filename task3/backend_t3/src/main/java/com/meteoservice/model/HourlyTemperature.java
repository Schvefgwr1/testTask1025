package com.meteoservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Температура на конкретный час
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HourlyTemperature {
    private String hour;
    private double temperature;
}

