package com.example.demo.domain;

import com.example.demo.web.MarsWeatherDto;
import com.example.demo.web.NasaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarsService {

    private final NasaClient nasaClient;

    public MarsWeatherDto getWeather() {
        return nasaClient.getWeather();
    }
}
