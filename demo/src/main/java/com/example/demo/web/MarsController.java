package com.example.demo.web;

import com.example.demo.domain.MarsService;
import com.example.demo.model.MarsDailyWeather;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MarsController {

    private final MarsService marsService;

    @GetMapping("/get7days")
    List<SolDataDto> getWeatherForLast7Days(){
        return marsService.aggregateWeatherForLast7Days();
    }

    @GetMapping("/get20days")
    List<SolDataDto> getWeatherForNext20Days(){
        return marsService.getWeatherFor20Days();
    }

    @GetMapping("/weather/details")
    SolDataDto getWeatherDetailsBySol(@RequestParam int sol){
        return marsService.getWeatherDetailsForSol(sol);
    }

    @DeleteMapping("/delete")
    List<MarsDailyWeather> removeRecords(){
        return marsService.removeRecords();
    }

    @PutMapping("/synchronizeData")
    void synchronizeData(){
        marsService.synchronizeData();
    }
}
