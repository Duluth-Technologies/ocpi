package com.duluthtechnologies.ocpi.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.duluthtechnologies.ocpi")
public class OcpiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OcpiApplication.class, args);
	}

}
