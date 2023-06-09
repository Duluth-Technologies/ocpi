package com.duluthtechnologies.ocpi.api.ocpi.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ocpi.ocpi-api")
public class OcpiApiProperties {

    private String externalUrl;
    
    private boolean serializeTimestampWithZ = true;

	public String getExternalUrl() {
		return externalUrl;
	}

	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}

	public boolean isSerializeTimestampWithZ() {
		return serializeTimestampWithZ;
	}

	public void setSerializeTimestampWithZ(boolean serializeTimestampWithZ) {
		this.serializeTimestampWithZ = serializeTimestampWithZ;
	}
	
	

   
}
