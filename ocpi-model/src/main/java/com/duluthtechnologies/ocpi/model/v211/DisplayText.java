package com.duluthtechnologies.ocpi.model.v211;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DisplayText(

		/**
		 * Language Code ISO 639-1
		 */
		@NotNull @Size(max = 2) String language,

		/**
		 * Text to be displayed to a end user. No markup, html etc. allowed.
		 */
		@NotNull @Size(max = 512) String text) {

}
