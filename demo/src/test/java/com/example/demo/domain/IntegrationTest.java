package com.example.demo.domain;

import com.example.demo.config.TestInitDatabase;
import com.example.demo.model.MarsDailyWeather;
import com.example.demo.repo.MarsRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Import(TestInitDatabase.class)
public class IntegrationTest {

    @Autowired
    private MarsRepo marsRepo;

    @Test
    void shouldFindMarsDailyWeatherBySol(){
        //when
        Optional<MarsDailyWeather> solFromDb = marsRepo.findBySol(7777);
        //then
        assertThat(solFromDb).isPresent()
                .get()
                .extracting(MarsDailyWeather::getSol)
                .isEqualTo(7777);
    }
}
