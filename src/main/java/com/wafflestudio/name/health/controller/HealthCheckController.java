package com.wafflestudio.name.health.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/health")
public class HealthCheckController {
    private final JdbcTemplate jdbcTemplate;

    @GetMapping("")
    public String healthCheck(){
        try{
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return "success";
        }
        catch(Exception e){
            return e.getMessage();
        }
    }
}
