package com.duluthtechnologies.ocpi.test.integration.cpo;

import org.springframework.beans.factory.annotation.Autowired;

import com.duluthtechnologies.ocpi.test.integration.AbstractTest;
import com.duluthtechnologies.ocpi.test.integration.OcpiContainerProvider.CPOTestInstance;

public abstract class AbstractCPOTest extends AbstractTest {

	@Autowired
	protected CPOTestInstance cpoTestInstance;

}
