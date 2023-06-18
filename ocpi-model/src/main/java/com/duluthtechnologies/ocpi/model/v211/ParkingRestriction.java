package com.duluthtechnologies.ocpi.model.v211;

/**
 * Represents parking restrictions according to the OCPI 2.1.1 standard.
 */
public enum ParkingRestriction {

	/**
	 * Reserved parking spot for electric vehicles.
	 */
	EV_ONLY,

	/**
	 * Parking is only allowed while plugged in (charging).
	 */
	PLUGGED,

	/**
	 * Reserved parking spot for disabled people with valid ID.
	 */
	DISABLED,

	/**
	 * Parking spot for customers/guests only, for example in case of a hotel or
	 * shop.
	 */
	CUSTOMERS,

	/**
	 * Parking spot only suitable for (electric) motorcycles or scooters.
	 */
	MOTORCYCLES;
}
