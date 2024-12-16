package com.example.demo.domain;

import com.example.demo.model.DataSyncInfo;
import com.example.demo.repo.DataSyncInfoRepo;
import com.example.demo.web.MarsWeatherDetailsDto;
import com.example.demo.web.MarsWeatherDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class MarsServiceTestSuit {

    private MarsService marsService;
    @Mock
    private DataSyncInfoRepo syncRepo;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        marsService = new MarsService(null,null,null,syncRepo);
    }

    @Test
    void testAllFieldsNotNull(){
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
    void testHasNullFields(){
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
    void testHasInvalidFields(){
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
    void testIsDataOutdated(){
        //given
        DataSyncInfo outdated = new DataSyncInfo(LocalDateTime.now().minusDays(10));
        //when
        when(syncRepo.findFirstByOrderByLastUpdateDesc()).thenReturn(Optional.of(outdated));
        //then
        assertTrue(marsService.isDataOutdated());
    }

    @Test
    void testDataIsNotOutdated(){
        //given
        DataSyncInfo outdated = new DataSyncInfo(LocalDateTime.now().minusDays(1));
        //when
        when(syncRepo.findFirstByOrderByLastUpdateDesc()).thenReturn(Optional.of(outdated));
        //then
        assertFalse(marsService.isDataOutdated());
    }
}
