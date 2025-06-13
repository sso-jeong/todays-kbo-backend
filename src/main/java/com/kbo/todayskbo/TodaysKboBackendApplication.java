package com.kbo.todayskbo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class TodaysKboBackendApplication {

	public static void main(String[] args) {

		SpringApplication.run(TodaysKboBackendApplication.class, args);
	}

}