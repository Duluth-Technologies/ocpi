module com.duluthtechnologies.ocpi.api.dto {
	requires jakarta.validation;
	requires com.fasterxml.jackson.annotation;
	requires com.duluthtechnologies.ocpi.core;

	exports com.duluthtechnologies.ocpi.api.dto;
	exports com.duluthtechnologies.ocpi.api.dto.v211;
}