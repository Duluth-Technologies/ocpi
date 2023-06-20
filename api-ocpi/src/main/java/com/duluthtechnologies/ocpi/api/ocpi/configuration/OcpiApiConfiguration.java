package com.duluthtechnologies.ocpi.api.ocpi.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.duluthtechnologies.ocpi.model.TimestampSerializer;

import jakarta.annotation.PostConstruct;

@Configuration
@EnableAspectJAutoProxy
public class OcpiApiConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(OcpiApiConfiguration.class);

	@Autowired
	private OcpiApiProperties ocpiApiProperties;

	@Bean("externalOcpiApiUrl")
	public String externalOcpiApiUrl() {
		return ocpiApiProperties.getExternalUrl();
	}

	@PostConstruct
	public void setTimestampSerializationConfiguration() {
		LOG.info("Setting OCPI API timestamp serialization with 'Z' to [{}]...", ocpiApiProperties.isSerializeTimestampWithZ());
		TimestampSerializer.shouldSerializeWithZ = ocpiApiProperties.isSerializeTimestampWithZ();
	}

}
