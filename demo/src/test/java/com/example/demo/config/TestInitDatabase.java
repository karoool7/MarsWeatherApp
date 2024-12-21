package com.example.demo.config;

import com.example.demo.model.MarsDailyWeather;
import com.example.demo.repo.MarsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@TestConfiguration
@RequiredArgsConstructor
public class TestInitDatabase {

    private final MarsRepo marsRepo;

    @Bean
    CommandLineRunner testInitDatabase(){
        return args -> {
            List<MarsDailyWeather> soles = new ArrayList<>();
            for (int i = 1; i<50; i++){
                MarsDailyWeather sol = new MarsDailyWeather(
                        null,
                        "2024-11-01",
                        7777 * i,
                        "33",
                        "Month 2",
                        "-30",
                        "5",
                        "555",
                        "High",
                        "Sunny",
                        "08:33",
                        "17:33",
                        "Moderate",
                        "-90",
                        "-1"
                );
                soles.add(sol);
            }
            marsRepo.saveAll(soles);
        };
    }
}
