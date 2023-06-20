package com.duluthtechnologies.ocpi.test.integration;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.Network;

import com.duluthtechnologies.ocpi.test.integration.OcpiContainerProvider.CPOTestInstance;
import com.duluthtechnologies.ocpi.test.integration.OcpiContainerProvider.EMSPTestInstance;

@Configuration
@ComponentScan(basePackages = "com.duluthtechnologies.ocpi.test")
public class SpringBootTestConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(SpringBootTestConfiguration.class);

	@Autowired
	OcpiContainerProvider ocpiContainerProvider;

	@Bean
	public Network network() {
		LOG.info("Creating network that will be used for test containers...");
		return Network.newNetwork();
	}

	@Bean
	CPOTestInstance cpoTestInstance(Network network) {
		String partyId = RandomStringUtils.random(3, true, false).toUpperCase();
		LOG.info("Creating CPO instance under test with party Id [{}]...", partyId);
		return ocpiContainerProvider.createCPOContainer(network, "FR", partyId, true);
	}

	@Bean
	EMSPTestInstance emspTestInstance(Network network) {
		String partyId = RandomStringUtils.random(3, true, false).toUpperCase();
		LOG.info("Creating EMSP instance under test with party Id [{}]...", partyId);
		return ocpiContainerProvider.createEMSPContainer(network, "FR", partyId);
	}

	@Bean
	ChromeDriver chromeDriver() {
		LOG.info("Creating ChromeDriver...");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		// Needed since Chrome 111 otherwise connection cannot be established
		options.addArguments("--remote-allow-origins=*");
		return new ChromeDriver(options);
	}

}
