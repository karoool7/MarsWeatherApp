package com.example.demo.domain;

import com.example.demo.config.TestInitDatabase;
import com.example.demo.model.MarsDailyWeather;
import com.example.demo.repo.MarsRepo;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Import(TestInitDatabase.class)
public class IntegrationTest {

    @Autowired
    private MarsRepo marsRepo;

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

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
