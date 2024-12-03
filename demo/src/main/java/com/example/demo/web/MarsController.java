package com.example.demo.web;

import com.example.demo.domain.MarsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MarsController {

    private final MarsService marsService;

    @GetMapping("/getWeather")
    MarsWeatherDto getWeather(){
        return marsService.getWeather();
    }
}
