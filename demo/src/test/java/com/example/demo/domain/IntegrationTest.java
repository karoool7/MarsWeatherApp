package com.example.demo.domain;

import com.example.demo.config.TestInitDatabase;
import com.example.demo.model.MarsDailyWeather;
import com.example.demo.repo.MarsRepo;
import com.example.demo.web.MarsWeatherDetailsDto;
import com.example.demo.web.MarsWeatherDto;
import com.example.demo.web.SolDataDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestInitDatabase.class)
public class IntegrationTest {

    @Autowired
    private MarsRepo marsRepo;
    @Autowired
    private MarsService marsService;
    @Autowired
    private TestRestTemplate restTemplate;

    @RegisterExtension
    static WireMockExtension wireMock =
            WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("nasa-client.url", () -> "http://localhost:" + wireMock.getPort());
    }

    @Test
    void shouldFindMarsDailyWeatherBySol() {
        //when
        Optional<MarsDailyWeather> solFromDb = marsRepo.findBySol(7777);
        //then
        assertThat(solFromDb).isPresent()
                .get()
                .extracting(MarsDailyWeather::getSol)
                .isEqualTo(7777);
        log.info("Wielkość bazy danych {}", marsRepo.count());
    }

    @Test
    void testGetWeatherFromOpenFeign() throws JsonProcessingException {
        //given
        String weatherDtoAsJson = createMarsWeatherDtoAsJson();
        WireMock.configureFor("localhost", wireMock.getPort());
        stubFor(get(urlMatching("/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(weatherDtoAsJson)));
        //when
        MarsWeatherDto response = marsService.fetchMarsWeatherFromNasa();
        //then
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.soles().size()).isEqualTo(1),
                () -> assertThat(response.soles().get(0).minTemp()).isEqualTo("-30"),
                () -> assertThat(response.soles().get(0).pressure()).isEqualTo("555")
        );
    }

    private static String createMarsWeatherDtoAsJson() throws JsonProcessingException {
        List<MarsWeatherDetailsDto> detailsDtos = new ArrayList<>();
        MarsWeatherDetailsDto dto = new MarsWeatherDetailsDto(
                "2024-11-01",
                "7777",
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
        detailsDtos.add(dto);
        MarsWeatherDto weatherDto = new MarsWeatherDto(detailsDtos);
        ObjectMapper objectMapper = new ObjectMapper();
        String weatherDtoAsJson = objectMapper.writeValueAsString(weatherDto);
        return weatherDtoAsJson;
    }

    @Test
    void shouldReturn7Soles(){
        //when
        List<SolDataDto> response = restTemplate.exchange(
                "/get7days",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SolDataDto>>() {
                }
        ).getBody();
        //then
        assertThat(response.size()).isEqualTo(7);
    }

    @Test
    void shouldReturnSolesOrderedBySolDesc(){
        //when
        List<MarsDailyWeather> response = marsRepo.findAllByOrderBySolDesc();
        //then
        assertThat(response)
                .extracting(MarsDailyWeather::getSol)
                .isSortedAccordingTo(Comparator.reverseOrder());
    }
}
