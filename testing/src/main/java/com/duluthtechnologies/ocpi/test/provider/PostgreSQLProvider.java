package com.duluthtechnologies.ocpi.test.provider;

import org.testcontainers.containers.Network;

import com.duluthtechnologies.ocpi.test.model.PostgreSQLTestInstance;

public interface PostgreSQLProvider {

	/**
	 * Creates a PostgreSQL instance to connect the main application on.
	 * 
	 * @param network
	 * @param networkAlias
	 * @return
	 */
	PostgreSQLTestInstance createPostgreSQLTestInstance(Network network, String networkAlias);

	/**
	 * Creates a PostgreSQL instance to connect the main application on.
	 *
	 * @return the postgre SQL test instance
	 */
	PostgreSQLTestInstance createPostgreSQLTestInstance();
}
