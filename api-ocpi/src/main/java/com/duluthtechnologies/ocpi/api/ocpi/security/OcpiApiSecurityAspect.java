package com.duluthtechnologies.ocpi.api.ocpi.security;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.duluthtechnologies.ocpi.api.ocpi.annotation.Authenticated;
import com.duluthtechnologies.ocpi.api.ocpi.annotation.Authenticated.AuthenticatedType;
import com.duluthtechnologies.ocpi.core.context.SecurityContext;

@Aspect
@Component
public class OcpiApiSecurityAspect {

	private static final Logger LOG = LoggerFactory.getLogger(OcpiApiSecurityAspect.class);

	@Around("@within(com.duluthtechnologies.ocpi.api.ocpi.annotation.Authenticated)")
	public Object enforceAuthenticated(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();

		Authenticated authenticated = method.getAnnotation(Authenticated.class);
		if ((authenticated.type() == AuthenticatedType.CPO) && (SecurityContext.getCPOKey() == null)) {
			String message = "CPO Authentication required.";
			LOG.error(message);
			throw new RuntimeException(message);
		} else if ((authenticated.type() == AuthenticatedType.EMSP) && (SecurityContext.getEMSPKey() == null)) {
			String message = "EMSP Authentication required.";
			LOG.error(message);
			throw new RuntimeException(message);
		}

		return joinPoint.proceed(joinPoint.getArgs());
	}

}
