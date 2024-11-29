package com.example.demo.web;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MarsWeatherDetailsDto(@JsonProperty("terrestrial_date") String terrestrialDate, String sol, String ls,
                                    String season, @JsonProperty("min_temp") String minTemp,
                                    @JsonProperty("max_temp") String maxTemp, String pressure,
                                    @JsonProperty("pressure_string") String pressureString,
                                    @JsonProperty("atmo_opacity") String atmoOpacity, String sunrise, String sunset,
                                    @JsonProperty("local_uv_irradiance_index") String localUvIrradianceIndex,
                                    @JsonProperty("min_gts_temp") String minGtsTemp,
                                    @JsonProperty("max_gts_temp") String maxGtsTemp) {
}
