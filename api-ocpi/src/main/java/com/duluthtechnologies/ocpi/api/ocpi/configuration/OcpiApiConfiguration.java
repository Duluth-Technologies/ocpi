package com.duluthtechnologies.ocpi.api.ocpi.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class OcpiApiConfiguration {

	@Autowired
	private OcpiApiProperties ocpiApiProperties;

	@Bean("externalOcpiApiUrl")
	public String externalOcpiApiUrl() {
		return ocpiApiProperties.getExternalUrl();
	}

}
