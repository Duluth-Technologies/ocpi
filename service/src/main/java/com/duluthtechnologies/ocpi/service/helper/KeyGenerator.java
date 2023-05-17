package com.duluthtechnologies.ocpi.service.helper;

import org.apache.commons.lang3.RandomStringUtils;

import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;

public class KeyGenerator {
	
	public static String generateKey(Class type) {
		if (type == RegisteredCPO.class) {
			return "cpo_" + RandomStringUtils.random(16, true, false).toLowerCase();
		} else {
			throw new RuntimeException();
		}
	}

}
