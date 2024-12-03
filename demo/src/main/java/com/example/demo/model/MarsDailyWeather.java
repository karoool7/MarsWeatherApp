package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class MarsDailyWeather {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String terrestrialDate;
    private String sol;
    private String ls;
    private String season;
    private String minTemp;
    private String maxTemp;
    private String pressure;
    private String pressureString;
    private String atmoOpacity;
    private String sunrise;
    private String sunset;
    private String localUvIrradianceIndex;
    private String minGtsTemp;
    private String maxGtsTemp;
}
