module com.duluthtechnologies.ocpi.core {
	exports com.duluthtechnologies.ocpi.core.model;
	exports com.duluthtechnologies.ocpi.core.model.v211;
	exports com.duluthtechnologies.ocpi.core.service;
	exports com.duluthtechnologies.ocpi.core.store;
	exports com.duluthtechnologies.ocpi.core.configuration;
	exports com.duluthtechnologies.ocpi.core.exception;

	requires jakarta.validation;
	requires transitive com.duluthtechnologies.ocpi;

}