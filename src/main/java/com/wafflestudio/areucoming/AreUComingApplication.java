package com.wafflestudio.areucoming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class AreUComingApplication {

	public static void main(String[] args) {
		SpringApplication.run(AreUComingApplication.class, args);
	}
}
