package com.duluthtechnologies.ocpi.service.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SecurityContextFiltered {

	Class<? extends SecurityContextFilter> filter();
}
