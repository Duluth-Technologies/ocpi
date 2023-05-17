module com.duluthtechnologies.ocpi.main {
	requires spring.boot.autoconfigure;
	requires spring.context;
	requires spring.beans;
	requires spring.boot;
	requires spring.core;
	requires java.instrument;
	requires java.sql;
	requires com.duluthtechnologies.ocpi.core;
	requires org.slf4j;

	opens com.duluthtechnologies.ocpi.main to spring.core, spring.beans, spring.context;
}