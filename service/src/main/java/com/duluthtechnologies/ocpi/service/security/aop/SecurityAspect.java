package com.duluthtechnologies.ocpi.service.security.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.duluthtechnologies.ocpi.service.security.SecurityContextFilter;
import com.duluthtechnologies.ocpi.service.security.SecurityContextFiltered;

@Aspect
@Component
public class SecurityAspect {

	private static final Logger LOG = LoggerFactory.getLogger(SecurityAspect.class);

	private List<SecurityContextFilter> filters;

	public SecurityAspect(List<SecurityContextFilter> filters) {
		super();
		this.filters = filters;
	}

	@Around("@within(com.duluthtechnologies.ocpi.service.security.ApplySecurityFiltering) || @annotation(com.duluthtechnologies.ocpi.service.security.ApplySecurityFiltering)")
	public Object enforceApplySecurityFiltering(ProceedingJoinPoint joinPoint) throws Throwable {
		applySecurityFiltering(joinPoint);
		return joinPoint.proceed(joinPoint.getArgs());
	}

	private void applySecurityFiltering(ProceedingJoinPoint joinPoint) {
		// Retrieve the method called
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		// Iterate over all arguments
		for (int i = 0; i < method.getParameterCount(); i++) {
			Object arg = joinPoint.getArgs()[i];
			for (Annotation annotation : method.getParameterAnnotations()[i]) {
				// Check if the parameter has an annotation
				if (annotation instanceof SecurityContextFiltered securityContextFiltered) {
					// Try to apply any of the available SecurityFilter
					for (SecurityContextFilter filter : filters) {
						// Check if the defined filter in the parameter annotation is the one we're
						// iterating over
						if (securityContextFiltered.filter().isInstance(filter)) {
							// CHeck if the argument passes the filter
							if (!filter.filter(arg)) {
								// Argument doesn't pass the filter, return a SecurityException.
								// TODO Make the message come from the Filter
								String message = "Method [%s] in class [%s] has parameter #%s annotated with filter of type [%s] but argument didn't pass filter."
										.formatted(method.getName(), method.getDeclaringClass().getSimpleName(),
												Integer.toString(i), filter.getClass().getSimpleName());
								LOG.error(message);
								throw new SecurityException(message);
							}
						}
					}
				}
			}
		}
	}

}
