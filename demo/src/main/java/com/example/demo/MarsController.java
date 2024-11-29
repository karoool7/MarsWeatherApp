package com.example.demo;

import com.example.demo.web.MarsWeatherDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MarsController {

    private final NasaClient nasaClient;

    @GetMapping("/getWeather")
    MarsWeatherDto getWeather(){
        return nasaClient.getWeather();
    }
}
