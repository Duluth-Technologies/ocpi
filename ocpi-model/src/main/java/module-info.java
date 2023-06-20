module com.duluthtechnologies.ocpi {
	exports com.duluthtechnologies.ocpi.model;
	exports com.duluthtechnologies.ocpi.model.v211;

	requires org.slf4j;
	requires jakarta.validation;
	requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.databind;

}