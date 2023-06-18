package com.duluthtechnologies.ocpi.service.security;

@FunctionalInterface
public interface SecurityContextFilter<T> {

	/**
	 * 
	 * @param object
	 * @return true if the filter is passed successfully, false otherwise.
	 */
	boolean filter(T object);

}
