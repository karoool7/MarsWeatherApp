package com.example.demo.web;

import com.example.demo.domain.MarsService;
import com.example.demo.model.MarsDailyWeather;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MarsController {

    private final MarsService marsService;

    @GetMapping("/get7days")
    List<SolDataDto> getWeatherForLast7Days(){
        return marsService.aggregateWeatherForLast7Days();
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
