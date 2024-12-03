package com.example.demo.domain;

import com.example.demo.model.MarsDailyWeather;
import com.example.demo.web.MarsWeatherDetailsDto;
import org.springframework.stereotype.Service;

@Service
public class MarsMapper {

    MarsDailyWeather toEntityFromDto(MarsWeatherDetailsDto record){
        return new MarsDailyWeather(
                null, // id generowane automatycznie przez bazę danych
                record.terrestrialDate(),
                record.sol(),
                record.ls(),
                record.season(),
                record.minTemp(),
                record.maxTemp(),
                record.pressure(),
                record.pressureString(),
                record.atmoOpacity(),
                record.sunrise(),
                record.sunset(),
                record.localUvIrradianceIndex(),
                record.minGtsTemp(),
                record.maxGtsTemp()
        );
    }
}
