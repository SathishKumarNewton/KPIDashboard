package com.prodian.rsgirms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RsgirmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(RsgirmsApplication.class, args);
	}

}
