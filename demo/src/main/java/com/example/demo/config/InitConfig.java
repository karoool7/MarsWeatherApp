package com.example.demo.config;

import com.example.demo.domain.MarsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitConfig {

    private final MarsService marsService;

    @Bean
    public CommandLineRunner initDatabase(){
        return args -> {
          log.info("Wykonuje automatyczną inicjalizację bazy danych");
          marsService.initData();
        };
    }
}
