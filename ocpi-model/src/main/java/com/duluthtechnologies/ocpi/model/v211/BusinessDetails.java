package com.duluthtechnologies.ocpi.model.v211;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record BusinessDetails(
		
		/**
		 * Name of the operator.
		 */
		@NotEmpty
		@Size(max = 100)
		String name, 
		
		/**
		 * Link to the operator’s website.
		 */
		@Size(max = 255)
		String website,
		
		/**
		 * Image link to the operator’s logo.
		 */
		@Valid
		Image logo) {

}
