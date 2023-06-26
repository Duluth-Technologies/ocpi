open module com.duluthtechnologies.ocpi.api.client {	
	requires spring.web;
	requires com.duluthtechnologies.ocpi;
	requires org.slf4j;
	requires spring.context;	
	requires spring.beans;
	requires spring.core;
	
	exports com.duluthtechnologies.ocpi.api.client;
}