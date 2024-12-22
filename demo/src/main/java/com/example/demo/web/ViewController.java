package com.example.demo.web;

import com.example.demo.domain.MarsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final MarsService marsService;

    @GetMapping("/home")
    String getHome(Model model){
        List<SolDataDto> soles = marsService.aggregateWeatherForLast7Days();
        model.addAttribute("soles", soles);
        return "index";
    }

    @GetMapping("/mars")
    String getMarsHome(){
        return "mars";
    }

    @GetMapping("/mars20days")
    String get20DaysWeather(){
        return "mars20days";
    }
}
