package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class MarsDailyWeather {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String terrestrialDate;
    private Integer sol;
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        MarsDailyWeather that = (MarsDailyWeather) object;
        return Objects.equals(terrestrialDate, that.terrestrialDate) && Objects.equals(sol, that.sol) && Objects.equals(ls, that.ls) && Objects.equals(season, that.season) && Objects.equals(minTemp, that.minTemp) && Objects.equals(maxTemp, that.maxTemp) && Objects.equals(pressure, that.pressure) && Objects.equals(pressureString, that.pressureString) && Objects.equals(atmoOpacity, that.atmoOpacity) && Objects.equals(sunrise, that.sunrise) && Objects.equals(sunset, that.sunset) && Objects.equals(localUvIrradianceIndex, that.localUvIrradianceIndex) && Objects.equals(minGtsTemp, that.minGtsTemp) && Objects.equals(maxGtsTemp, that.maxGtsTemp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(terrestrialDate, sol, ls, season, minTemp, maxTemp, pressure, pressureString, atmoOpacity, sunrise, sunset, localUvIrradianceIndex, minGtsTemp, maxGtsTemp);
    }
}
