package com.duluthtechnologies.ocpi.model.v211;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record Image(

		/**
		 * URL from where the image data can be fetched through a web browser.
		 */
		@Size(max = 255) String url,

		/**
		 * URL from where a thumbnail of the image can be fetched through a web browser.
		 */
		@Size(max = 255) String thumbnail,

		/**
		 * Describes what the image is used for.
		 */
		@Valid ImageCategory category,

		/**
		 * Image type like: gif, jpeg, png, svg
		 */
		@Size(max = 4) String type,

		/**
		 * Width of the full scale image
		 */
		Integer width,

		/**
		 * Height of the full scale image
		 */
		Integer height) {

}
