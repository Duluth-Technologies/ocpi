package com.duluthtechnologies.ocpi.api.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ocpi.api")
public class ApiProperties {

	private Swagger swagger;

	public Swagger getSwagger() {
		return swagger;
	}

	public void setSwagger(Swagger swagger) {
		this.swagger = swagger;
	}

	public static class Swagger {

		private boolean activated = false;

		public boolean isActivated() {
			return activated;
		}

		public void setActivated(boolean activated) {
			this.activated = activated;
		}

	}

}
