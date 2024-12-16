package com.example.demo.domain;

import com.example.demo.web.MarsWeatherDetailsDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MarsServiceTestSuit {

    @Test
    void testAllFieldsNotNull(){
        MarsService marsService = new MarsService(null,null,null,null);
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

        assertTrue(marsService.hasNoNullFields(dto));
    }

    @Test
    void testHasNullFields(){
        MarsService marsService = new MarsService(null,null,null,null);
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

        assertFalse(marsService.hasNoNullFields(dto));
    }

    @Test
    void testHasInvalidFields(){
        MarsService marsService = new MarsService(null,null,null,null);
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

        assertFalse(marsService.hasNoNullFields(dto));
    }
}
