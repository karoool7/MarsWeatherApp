package com.example.demo.repo;

import com.example.demo.model.MarsDailyWeather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarsRepo extends JpaRepository<MarsDailyWeather, Long> {

    List<MarsDailyWeather> findFirst7ByOrderBySolDesc();

    Optional<MarsDailyWeather> findBySol(int sol);
}
