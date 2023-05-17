package com.duluthtechnologies.ocpi.test.integration.emsp;

import org.springframework.beans.factory.annotation.Autowired;

import com.duluthtechnologies.ocpi.test.integration.AbstractTest;
import com.duluthtechnologies.ocpi.test.integration.OcpiContainerProvider.EMSPTestInstance;

public abstract class AbstractEMSPTest extends AbstractTest {

	@Autowired
	protected EMSPTestInstance emspTestInstance;

}
