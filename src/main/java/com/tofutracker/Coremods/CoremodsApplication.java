package com.tofutracker.Coremods;

import com.tofutracker.Coremods.config.IgdbConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties(IgdbConfig.class)
@EnableAsync
public class CoremodsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoremodsApplication.class, args);
	}

}
