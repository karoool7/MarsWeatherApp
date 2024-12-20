package com.example.demo.web;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "nasa-client", url = "${nasa-client.url}")
public interface NasaClient {

    @GetMapping
    MarsWeatherDto getWeather();
}
