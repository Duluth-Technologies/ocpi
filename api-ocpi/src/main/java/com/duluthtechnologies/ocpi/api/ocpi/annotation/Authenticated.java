package com.duluthtechnologies.ocpi.api.ocpi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Authenticated {

	AuthenticatedType type();

	public enum AuthenticatedType {
		CPO, EMSP
	}

}
