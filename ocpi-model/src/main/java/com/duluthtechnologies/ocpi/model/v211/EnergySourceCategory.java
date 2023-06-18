package com.duluthtechnologies.ocpi.model.v211;

/**
 * Represents an energy source category according to the OCPI 2.1.1 standard.
 */
public enum EnergySourceCategory {
	/**
	 * Nuclear power sources.
	 */
	NUCLEAR,

	/**
	 * All kinds of fossil power sources.
	 */
	GENERAL_FOSSIL,

	/**
	 * Fossil power from coal.
	 */
	COAL,

	/**
	 * Fossil power from gas.
	 */
	GAS,

	/**
	 * All kinds of regenerative power sources.
	 */
	GENERAL_GREEN,

	/**
	 * Regenerative power from PV.
	 */
	SOLAR,

	/**
	 * Regenerative power from wind turbines.
	 */
	WIND,

	/**
	 * Regenerative power from water turbines.
	 */
	WATER
}
