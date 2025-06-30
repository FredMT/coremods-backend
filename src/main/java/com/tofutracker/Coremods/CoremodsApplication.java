package com.tofutracker.Coremods;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class CoremodsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoremodsApplication.class, args);
	}

	@Bean
	public CommandLineRunner logTest() {
		return args -> {
			log.info("This is a test manual log that should go to the manual.log file");
			log.error("This is a test error log that should go to the manual.log file");
		};
	}

}
