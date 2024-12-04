package com.example.demo.domain;

import com.example.demo.repo.MarsRepo;
import com.example.demo.web.MarsWeatherDetailsDto;
import com.example.demo.web.MarsWeatherDto;
import com.example.demo.web.NasaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarsService {

    private final NasaClient nasaClient;

    public MarsWeatherDto getWeather() {
        return nasaClient.getWeather();
    }

    private Optional<List<MarsWeatherDetailsDto>> getValidSolesIfPresent(MarsWeatherDto marsWeatherDto) {
        return Optional.ofNullable(marsWeatherDto)
                .map(MarsWeatherDto::soles)
                .map(soles -> soles.stream()
                        .filter(this::hasNoNullFields)
                        .toList())
                .filter(soles -> !soles.isEmpty());
    }

    private boolean hasNoNullFields(MarsWeatherDetailsDto sol) {
        return sol != null &&
                sol.terrestrialDate() != null &&
                sol.sol() != null &&
                sol.ls() != null &&
                sol.season() != null &&
                sol.minTemp() != null &&
                sol.maxTemp() != null &&
                sol.pressure() != null &&
                sol.pressureString() != null &&
                sol.atmoOpacity() != null &&
                sol.sunrise() != null &&
                sol.sunset() != null &&
                sol.localUvIrradianceIndex() != null &&
                sol.minGtsTemp() != null &&
                sol.maxGtsTemp() != null;
    }
}
