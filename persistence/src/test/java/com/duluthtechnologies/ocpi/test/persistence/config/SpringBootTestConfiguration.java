package com.duluthtechnologies.ocpi.test.persistence.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;

import com.duluthtechnologies.ocpi.persistence.configuration.OcpiPersistenceProperties;
import com.duluthtechnologies.ocpi.test.model.PostgreSQLTestInstance;
import com.duluthtechnologies.ocpi.test.provider.PostgreSQLProvider;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.duluthtechnologies.ocpi.persistence", "com.duluthtechnologies.ocpi.test" })
public class SpringBootTestConfiguration {

	@Bean
	@Primary
	public OcpiPersistenceProperties ocpiPersistencePropertiesForTest(PostgreSQLProvider postgreSQLProvider) {
		OcpiPersistenceProperties ocpiPersistenceProperties = new OcpiPersistenceProperties();
		ocpiPersistenceProperties.setDriverClassName("org.postgresql.Driver");
		PostgreSQLTestInstance postgreSQLTestInstance = postgreSQLProvider.createPostgreSQLTestInstance();
		ocpiPersistenceProperties.setJdbcUrl(
				postgreSQLTestInstance.getExternalJdbcUrl() + "/" + postgreSQLTestInstance.getDatabaseName());
		ocpiPersistenceProperties.setUsername(postgreSQLTestInstance.getDatabaseUsername());
		ocpiPersistenceProperties.setPassword(postgreSQLTestInstance.getDatabasePassword());
		ocpiPersistenceProperties.setEncryptionPassword("test");
		return ocpiPersistenceProperties;
	}

}
