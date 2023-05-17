module com.duluthtechnologies.ocpi.api {
	requires spring.context;
	requires spring.web;
	requires spring.beans;
	requires spring.core;
	requires io.swagger.v3.oas.annotations;
	requires jakarta.validation;
	requires com.duluthtechnologies.ocpi.api.dto;
	requires com.duluthtechnologies.ocpi.core;
	requires org.mapstruct;
	requires java.compiler;
	requires org.slf4j;
	requires spring.boot;
	requires org.springdoc.openapi.common;
	requires io.swagger.v3.oas.models;
	requires spring.webmvc;
}