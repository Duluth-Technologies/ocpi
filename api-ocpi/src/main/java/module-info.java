module com.duluthtechnologies.ocpi.api.ocpi {
	requires spring.context;
	requires spring.boot.autoconfigure;
	requires spring.web;
	requires com.duluthtechnologies.ocpi.core;
	requires org.slf4j;
	requires spring.core;
	requires spring.beans;
	requires jakarta.servlet;
	requires jakarta.validation;
	requires jakarta.annotation;
	requires org.aspectj.weaver;
	requires spring.boot;
	requires io.swagger.v3.oas.annotations;
	requires com.duluthtechnologies.ocpi;
	requires org.mapstruct;

}