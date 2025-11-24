package com.ep.isw.infrastructure.config;

import com.ep.isw.domain.service.CommentPolicy;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public CommentPolicy commentPolicy() {
        return new CommentPolicy();
    }
}
