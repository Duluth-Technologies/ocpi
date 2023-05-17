module com.duluthtechnologies.ocpi.service {
	requires spring.context;
	requires org.apache.commons.lang3;
	requires com.duluthtechnologies.ocpi.core;
	requires jakarta.validation;
	requires jakarta.transaction;
	requires org.slf4j;
	requires spring.web;
	requires spring.core;
	requires spring.beans;
	requires org.mapstruct;
	requires java.compiler;
	requires com.duluthtechnologies.ocpi;
	requires org.apache.httpcomponents.client5.httpclient5;
}