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

    private MarsWeatherDetailsDto toDtoFromEntity(MarsDailyWeather entity){
        return new MarsWeatherDetailsDto(
                entity.getTerrestrialDate(),
                entity.getSol(),
                entity.getLs(),
                entity.getSeason(),
                entity.getMinTemp(),
                entity.getMaxTemp(),
                entity.getPressure(),
                entity.getPressureString(),
                entity.getAtmoOpacity(),
                entity.getSunrise(),
                entity.getSunset(),
                entity.getLocalUvIrradianceIndex(),
                entity.getMinGtsTemp(),
                entity.getMaxGtsTemp()
        );
    }

    List<MarsWeatherDetailsDto> toDtoListFromEntity(List<MarsDailyWeather> entities){
        return entities.stream().map(this::toDtoFromEntity).toList();
    }
}
