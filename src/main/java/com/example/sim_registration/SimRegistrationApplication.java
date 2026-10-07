package com.example.sim_registration;

import com.example.sim_registration.config.NidaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(NidaProperties.class)
public class SimRegistrationApplication {

	public static void main(String[] args) {

		SpringApplication.run(SimRegistrationApplication.class, args);
	}

}
