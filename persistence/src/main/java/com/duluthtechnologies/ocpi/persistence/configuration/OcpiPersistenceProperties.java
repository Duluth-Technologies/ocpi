package com.duluthtechnologies.ocpi.persistence.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ocpi.persistence")
public class OcpiPersistenceProperties {

	private String driverClassName;

	private String encryptionPassword;

	private String jdbcUrl;

	private long maxConnectionLifeTimeInMs = 600_000;

	private String password;

	private String username;

	public String getDriverClassName() {
		return driverClassName;
	}

	public String getEncryptionPassword() {
		return encryptionPassword;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public long getMaxConnectionLifeTimeInMs() {
		return maxConnectionLifeTimeInMs;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public void setEncryptionPassword(String encryptionPassword) {
		this.encryptionPassword = encryptionPassword;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public void setMaxConnectionLifeTimeInMs(long maxConnectionLifeTimeInMs) {
		this.maxConnectionLifeTimeInMs = maxConnectionLifeTimeInMs;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
