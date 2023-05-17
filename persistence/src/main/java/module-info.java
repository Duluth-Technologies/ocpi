open module com.duluthtechnologies.ocpi.persistence {
	requires spring.context;
	requires spring.boot;
	requires spring.beans;
	requires jakarta.persistence;
	requires jakarta.validation;
	requires jakarta.annotation;
	requires org.mapstruct;
	requires spring.data.commons;
	requires spring.data.jpa;
	requires com.duluthtechnologies.ocpi.core;
	requires org.slf4j;
	requires jakarta.transaction;
	requires spring.tx;
	requires spring.boot.autoconfigure;
	requires java.compiler;
	requires com.zaxxer.hikari;
	requires java.sql;

	exports com.duluthtechnologies.ocpi.persistence.configuration;
}