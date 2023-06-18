package com.duluthtechnologies.ocpi.model.v211;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record GeoLocation(

		@Pattern(regexp = "-?[0-9]{1,2}\\.[0-9]{6}") @NotNull String latitude,

		@Pattern(regexp = "-?[0-9]{1,3}\\.[0-9]{6}") @NotNull String longitude) {

}
