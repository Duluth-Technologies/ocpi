package com.duluthtechnologies.ocpi.service.security.filter;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.duluthtechnologies.ocpi.core.context.SecurityContext;
import com.duluthtechnologies.ocpi.service.security.SecurityContextFilter;

@Component
public class RegisteredOperatorKeyFilter implements SecurityContextFilter<String> {

	@Override
	public boolean filter(String registeredOperatorKey) {
		return Objects.equals(SecurityContext.getCPOKey(), registeredOperatorKey) || Objects.equals(SecurityContext.getEMSPKey(), registeredOperatorKey);
	}

}
