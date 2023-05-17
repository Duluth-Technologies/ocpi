package com.duluthtechnologies.ocpi.api.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfiguration implements BeanPostProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(SwaggerConfiguration.class);

	private final ApiProperties apiProperties;

	public SwaggerConfiguration(ApiProperties apiProperties) {
		this.apiProperties = apiProperties;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof SwaggerUiConfigProperties swaggerUiConfigProperties) {
			boolean swaggerActivated = apiProperties.getSwagger().isActivated();
			LOG.info("Swagger {} activated.", swaggerActivated ? "is" : "is NOT");
			// Override the Swagger property with the one provided in our own API property
			swaggerUiConfigProperties.setEnabled(swaggerActivated);
		}
		return bean;
	}

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI().info(new Info().title("OCPI API").description("Documentation of API v1.0").version("1.0"));
	}

}
