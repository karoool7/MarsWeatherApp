package com.example.demo.domain;

import com.example.demo.model.MarsDailyWeather;
import com.example.demo.repo.MarsRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class IntegrationTest {

    @Autowired
    private MarsRepo marsRepo;

    @Test
    void testSaveAndFindSol(){
        // given
        MarsDailyWeather sol = new MarsDailyWeather(
                null,
                "2024-11-01",
                7777,
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
        //when
        marsRepo.save(sol);
        Optional<MarsDailyWeather> solFromDb = marsRepo.findBySol(7777);
        //then
        assertThat(solFromDb).isPresent()
                .get()
                .extracting(MarsDailyWeather::getSol)
                .isEqualTo(7777);
    }
}
