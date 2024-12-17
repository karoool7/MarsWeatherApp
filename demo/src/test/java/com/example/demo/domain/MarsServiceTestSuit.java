package com.example.demo.domain;

import com.example.demo.exception.NoDataFoundException;
import com.example.demo.model.DataSyncInfo;
import com.example.demo.model.MarsDailyWeather;
import com.example.demo.repo.DataSyncInfoRepo;
import com.example.demo.repo.MarsRepo;
import com.example.demo.web.MarsWeatherDetailsDto;
import com.example.demo.web.SolDataDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class MarsServiceTestSuit {

    private MarsService marsService;
    @Mock
    private DataSyncInfoRepo syncRepo;
    @Mock
    private MarsRepo marsRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        marsService = new MarsService(null, null, marsRepo, syncRepo);
    }

    @Test
    void testAllFieldsNotNull() {
        //given
        MarsWeatherDetailsDto dto = new MarsWeatherDetailsDto(
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test"
        );
        //when&then
        assertTrue(marsService.hasNoNullFields(dto));
    }

    @Test
    void testHasNullFields() {
        //given
        MarsWeatherDetailsDto dto = new MarsWeatherDetailsDto(
                null,
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test"
        );
        //when&then
        assertFalse(marsService.hasNoNullFields(dto));
    }

    @Test
    void testHasInvalidFields() {
        //given
        MarsWeatherDetailsDto dto = new MarsWeatherDetailsDto(
                "--",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test"
        );
        //when&then
        assertFalse(marsService.hasNoNullFields(dto));
    }

    @Test
    void testIsDataOutdated() {
        //given
        DataSyncInfo outdated = new DataSyncInfo(LocalDateTime.now().minusDays(10));
        //when
        when(syncRepo.findFirstByOrderByLastUpdateDesc()).thenReturn(Optional.of(outdated));
        //then
        assertTrue(marsService.isDataOutdated());
    }

    @Test
    void testDataIsNotOutdated() {
        //given
        DataSyncInfo outdated = new DataSyncInfo(LocalDateTime.now().minusDays(1));
        //when
        when(syncRepo.findFirstByOrderByLastUpdateDesc()).thenReturn(Optional.of(outdated));
        //then
        assertFalse(marsService.isDataOutdated());
    }

    @Test
    void testCalculateAvgTemps() {
        //given
        Map<Integer, Integer> maxTempMap = new HashMap<>();
        Map<Integer, Integer> minTempMap = new HashMap<>();
        int totalMartianYears = 2;

        maxTempMap.put(1, 30);
        maxTempMap.put(2, 40);
        maxTempMap.put(3, 50);

        minTempMap.put(1, -10);
        minTempMap.put(2, -20);
        minTempMap.put(3, -30);
        //when
        marsService.calculateAvgTempsForDays(maxTempMap, totalMartianYears, minTempMap);
        //then
        assertEquals(15, maxTempMap.get(1));
        assertEquals(20, maxTempMap.get(2));
        assertEquals(25, maxTempMap.get(3));
        assertEquals(-5, minTempMap.get(1));
        assertEquals(-10, minTempMap.get(2));
        assertEquals(-15, minTempMap.get(3));
    }

    @Test
    void shouldReturn20WeatherDataItems() {
        //given
        List<MarsDailyWeather> soles = new ArrayList<>();
        for (int i = 1; i < 700; i++) {
            soles.add(new MarsDailyWeather(
                    null,
                    null,
                    i,
                    null,
                    null,
                    "-15",
                    "8",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            ));
        }
        //when
        when(marsRepo.findAllByOrderBySolDesc()).thenReturn(soles);
        List<SolDataDto> result = marsService.getWeatherFor20Days();
        //then
        assertNotNull(result);
        assertEquals(20, result.size());
    }

    @Test
    void shouldThrowNoDataFoundExceptionWhenSolesListIsEmpty(){
        //given
        List<MarsDailyWeather> emptyList = new ArrayList<>();
        //when
        NoDataFoundException exception = assertThrows(
                NoDataFoundException.class,
                () -> marsService.getWeatherFor20Days()
        );
        //then
        assertNotNull(exception);
        assertEquals("No weather data available", exception.getMessage());
    }
}
