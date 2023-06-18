package com.duluthtechnologies.ocpi.persistence.configuration;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableConfigurationProperties
@EnableTransactionManagement
@EntityScan("com.duluthtechnologies.ocpi.persistence.entity")
@EnableEnversRepositories
@EnableJpaRepositories(value = "com.duluthtechnologies.ocpi.persistence.jpa", repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
@EnableJpaAuditing
public class OcpiPersistenceConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(OcpiPersistenceConfiguration.class);

	@Bean
	public DataSource dataSource(OcpiPersistenceProperties properties) {
		LOG.debug("Configuring DataSource with driver class name [{}] and JDBC URL [{}] and username [{}]...",
				properties.getDriverClassName(), properties.getJdbcUrl(), properties.getUsername());
		HikariDataSource hikariDataSource = new HikariDataSource();
		hikariDataSource.setDriverClassName(properties.getDriverClassName());
		hikariDataSource.setJdbcUrl(properties.getJdbcUrl());
		hikariDataSource.setUsername(properties.getUsername());
		hikariDataSource.setPassword(properties.getPassword());
		hikariDataSource.setMaxLifetime(properties.getMaxConnectionLifeTimeInMs());
		hikariDataSource.setLeakDetectionThreshold(10_000);
		return hikariDataSource;
	}

	@Bean("encryption-password")
	public String encryptionPassword(OcpiPersistenceProperties properties) {
		return properties.getEncryptionPassword();
	}

}
