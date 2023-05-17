package com.duluthtechnologies.ocpi.test.model;

public interface PostgreSQLTestInstance {

	String getExternalJdbcUrl();

	String getInternalJdbcUrl();

	String getDatabaseName();

	String getDatabaseUsername();

	String getDatabasePassword();

	void teardown();

}
