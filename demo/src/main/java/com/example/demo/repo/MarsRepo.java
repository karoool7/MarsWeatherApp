package com.example.demo.repo;

import com.example.demo.model.MarsDailyWeather;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarsRepo extends JpaRepository<MarsDailyWeather, Long> {
}
