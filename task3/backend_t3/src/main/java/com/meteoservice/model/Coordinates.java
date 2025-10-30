package com.meteoservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Географические координаты города
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {
    private double latitude;
    private double longitude;
}

