package com.hatim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RobotzApplication {

	public static void main(String[] args) {
		SpringApplication.run(RobotzApplication.class, args);
	}
}
