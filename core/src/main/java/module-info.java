module com.duluthtechnologies.ocpi.core {
	exports com.duluthtechnologies.ocpi.core.model;
	exports com.duluthtechnologies.ocpi.core.model.v211;
	exports com.duluthtechnologies.ocpi.core.service;
	exports com.duluthtechnologies.ocpi.core.store;
	exports com.duluthtechnologies.ocpi.core.configuration;
	exports com.duluthtechnologies.ocpi.core.exception;
	exports com.duluthtechnologies.ocpi.core.context;

	requires jakarta.validation;
	requires transitive com.duluthtechnologies.ocpi;

}