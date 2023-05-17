package com.duluthtechnologies.ocpi.test.provider.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;

import com.duluthtechnologies.ocpi.test.model.PostgreSQLTestInstance;
import com.duluthtechnologies.ocpi.test.provider.PostgreSQLProvider;

@Component
public class PostgreSQLProviderImpl implements PostgreSQLProvider {

	private static final Logger LOG = LoggerFactory.getLogger(PostgreSQLProviderImpl.class);

	private static final String POSTGRES_DATABASE_NAME = "test";
	private static final String POSTGRES_DATABASE_PASSWORD = "test";
	private static final String POSTGRES_DATABASE_USERNAME = "test";
	private static final String POSTGRES_DOCKER_IMAGE = "postgres:14-alpine3.15";

	@Override
	public PostgreSQLTestInstance createPostgreSQLTestInstance() {
		return createPostgreSQLTestInstance(null, null);
	}

	@Override
	public PostgreSQLTestInstance createPostgreSQLTestInstance(Network network, String networkAlias) {
		LOG.info("Creating PostgreSQLTestInstance...");
		PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(POSTGRES_DOCKER_IMAGE)
				.withDatabaseName(POSTGRES_DATABASE_NAME).withUsername(POSTGRES_DATABASE_USERNAME)
				.withPassword(POSTGRES_DATABASE_PASSWORD);
		if (network != null) {
			postgresContainer.withNetwork(network).withNetworkAliases(networkAlias);
		}
		postgresContainer.start();
		return new PostgreSQLTestInstance() {

			@Override
			public String getExternalJdbcUrl() {
				return "jdbc:postgresql://" + postgresContainer.getHost() + ":"
						+ postgresContainer.getFirstMappedPort();
			}

			@Override
			public String getInternalJdbcUrl() {
				return "jdbc:postgresql://" + networkAlias + ":5432";
			}

			@Override
			public String getDatabaseName() {
				return POSTGRES_DATABASE_NAME;
			}

			@Override
			public String getDatabaseUsername() {
				return POSTGRES_DATABASE_USERNAME;
			}

			@Override
			public String getDatabasePassword() {
				return POSTGRES_DATABASE_PASSWORD;
			}

			@Override
			public void teardown() {
				postgresContainer.stop();
			}

		};
	}

}
