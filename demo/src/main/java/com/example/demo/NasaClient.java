package com.example.demo;

import com.example.demo.web.MarsWeatherDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "nasa-client", url = "https://mars.nasa.gov/rss/api/?feed=weather&category=msl&feedtype=json")
public interface NasaClient {

    @GetMapping
    MarsWeatherDto getWeather();
}