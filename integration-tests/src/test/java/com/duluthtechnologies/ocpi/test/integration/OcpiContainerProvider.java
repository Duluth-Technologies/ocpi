package com.duluthtechnologies.ocpi.test.integration;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import com.duluthtechnologies.ocpi.test.model.PostgreSQLTestInstance;
import com.duluthtechnologies.ocpi.test.provider.PostgreSQLProvider;

@Component
public class OcpiContainerProvider {

	private static final String LOG_LEVEL = "INFO";

	private static final Logger LOG = LoggerFactory.getLogger(OcpiContainerProvider.class);

	@Autowired
	PostgreSQLProvider postgreSQLProvider;

	public CPOTestInstance createCPOContainer(Network network, String countryCode, String partyId) {
		String cpoId = "cpo-" + countryCode + "-" + partyId;
		LOG.info("Creating PostgreSQL database for CPO container [{}]...", cpoId);
		PostgreSQLTestInstance postgreSQLTestInstance = postgreSQLProvider.createPostgreSQLTestInstance(network,
				"postgres-" + countryCode + "-" + partyId);
		LOG.info("Created PostgreSQL database for CPO container [{}].", cpoId);

		LOG.info("Starting CPO container [{}]...", cpoId);
		GenericContainer cpoContainer = new GenericContainer("com.duluthtechnologies.ocpi/main:0.0.1-SNAPSHOT")
				.withEnv("POSTGRESQL_USERNAME", postgreSQLTestInstance.getDatabaseUsername())
				.withEnv("POSTGRESQL_PASSWORD", postgreSQLTestInstance.getDatabasePassword())
				.withEnv("JDBC_URL",
						postgreSQLTestInstance.getInternalJdbcUrl() + "/" + postgreSQLTestInstance.getDatabaseName())
				.withEnv("DRIVER_CLASS_NAME", "org.postgresql.Driver").withEnv("ENCRYPTION_PASSWORD", "test")
				.withEnv("EXTERNAL_URL", "http://" + cpoId + ":8080").withEnv("OCPI_CPO_COUNTRYCODE", "FR")
				.withEnv("OCPI_API_SWAGGER_ACTIVATED", "true").withEnv("OCPI_CPO_PARTYID", partyId)
				.withEnv("OCPI_CPO_ENABLED", "true").withEnv("OCPI_NAME", partyId)
				.withEnv("LOGGING_LEVEL_ROOT", LOG_LEVEL).withNetwork(network).withNetworkAliases(cpoId)
				.withExposedPorts(8080)
				.waitingFor(Wait.forHttp("/actuator/health").forPort(8080).forStatusCode(200)
						.withStartupTimeout(Duration.ofSeconds(60)))
				.withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(cpoId)));
		cpoContainer.start();
		LOG.info("Started CPO container.");

		return new CPOTestInstance() {

			@Override
			public String getExternalUrl() {
				return "http://" + cpoContainer.getHost() + ":" + cpoContainer.getFirstMappedPort();
			}

			@Override
			public String getInternalUrl() {
				return "http://" + cpoId + ":8080";
			}

			@Override
			public String getCountryCode() {
				return countryCode;
			}

			@Override
			public String getPartyId() {
				return partyId;
			}

			@Override
			public void teardown() {
				LOG.info("Stopping CPO container [{}]...", cpoId);
				cpoContainer.stop();
				LOG.info("Stopped CPO container [{}].", cpoId);

				LOG.info("Stopping PostgreSQL database of CPO container [{}]...", cpoId);
				postgreSQLTestInstance.teardown();
				LOG.info("Stopped PostgreSQL database of CPO container [{}]...", cpoId);
			}

		};
	}

	public EMSPTestInstance createEMSPContainer(Network network, String countryCode, String partyId) {
		String emspId = "emsp-" + countryCode + "-" + partyId;

		LOG.info("Creating PostgreSQL database for EMSP container [{}]...", emspId);
		PostgreSQLTestInstance postgreSQLTestInstance = postgreSQLProvider.createPostgreSQLTestInstance(network,
				"postgres-" + countryCode + "-" + partyId);
		LOG.info("Created PostgreSQL database for EMSP container [{}].", emspId);

		LOG.info("Starting EMSP container with country code [{}] and partyId [{}]...", countryCode, partyId);
		GenericContainer emspContainer = new GenericContainer("com.duluthtechnologies.ocpi/main:0.0.1-SNAPSHOT")
				.withEnv("POSTGRESQL_USERNAME", postgreSQLTestInstance.getDatabaseUsername())
				.withEnv("POSTGRESQL_PASSWORD", postgreSQLTestInstance.getDatabasePassword())
				.withEnv("JDBC_URL",
						postgreSQLTestInstance.getInternalJdbcUrl() + "/" + postgreSQLTestInstance.getDatabaseName())
				.withEnv("DRIVER_CLASS_NAME", "org.postgresql.Driver").withEnv("ENCRYPTION_PASSWORD", "test")
				.withEnv("EXTERNAL_URL", "http://" + emspId + ":8080").withEnv("OCPI_EMSP_COUNTRYCODE", "FR")
				.withEnv("OCPI_API_SWAGGER_ACTIVATED", "true").withEnv("OCPI_EMSP_PARTYID", partyId)
				.withEnv("OCPI_EMSP_ENABLED", "true").withEnv("OCPI_NAME", partyId)
				.withEnv("LOGGING_LEVEL_ROOT", LOG_LEVEL).withNetwork(network).withNetworkAliases(emspId)
				.withExposedPorts(8080)
				.waitingFor(Wait.forHttp("/actuator/health").forPort(8080).forStatusCode(200)
						.withStartupTimeout(Duration.ofSeconds(60)))
				.withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(emspId)));
		emspContainer.start();
		LOG.info("Started EMSP container.");

		return new EMSPTestInstance() {

			@Override
			public String getExternalUrl() {
				return "http://" + emspContainer.getHost() + ":" + emspContainer.getFirstMappedPort();
			}

			@Override
			public String getInternalUrl() {
				return "http://" + emspId + ":8080";
			}

			@Override
			public String getCountryCode() {
				return countryCode;
			}

			@Override
			public String getPartyId() {
				return partyId;
			}

			@Override
			public void teardown() {
				LOG.info("Stopping EMSP container [{}]...", emspId);
				emspContainer.stop();
				LOG.info("Stopped EMSP container [{}].", emspId);

				LOG.info("Stopping PostgreSQL database of EMSP container [{}]...", emspId);
				postgreSQLTestInstance.teardown();
				LOG.info("Stopped PostgreSQL database of EMSP container [{}]...", emspId);
			}

		};
	}

	public static interface EMSPTestInstance {

		String getExternalUrl();

		String getInternalUrl();

		String getCountryCode();

		String getPartyId();

		void teardown();
	}

	public static interface CPOTestInstance {

		String getExternalUrl();

		String getInternalUrl();

		String getCountryCode();

		String getPartyId();

		void teardown();
	}

}
