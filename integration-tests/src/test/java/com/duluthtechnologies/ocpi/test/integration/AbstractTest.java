package com.duluthtechnologies.ocpi.test.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.Network;

@SpringBootTest(classes = SpringBootTestConfiguration.class)
public class AbstractTest {

	@Autowired
	protected OcpiContainerProvider ocpiContainerProvider;

	@Autowired
	protected Network network;

}
