package com.example.demo.domain;

import com.example.demo.model.MarsDailyWeather;
import com.example.demo.web.MarsWeatherDetailsDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarsMapper {

    MarsDailyWeather toEntityFromDto(MarsWeatherDetailsDto record){
        return new MarsDailyWeather(
                null, // id generowane automatycznie przez bazę danych
                record.terrestrialDate(),
                convertStringToInteger(record.sol()),
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

    private Integer convertStringToInteger(String solString){
        if (solString != null && solString.matches("-?\\d+")){
            return Integer.parseInt(solString);
        }
        throw new IllegalArgumentException("Invalid sol value: " + solString);
    }
}
