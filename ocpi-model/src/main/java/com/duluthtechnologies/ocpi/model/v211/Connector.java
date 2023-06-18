package com.duluthtechnologies.ocpi.model.v211;

import java.time.Instant;

import com.duluthtechnologies.ocpi.model.v211.Connector.ConnectorFormat;
import com.duluthtechnologies.ocpi.model.v211.Connector.ConnectorType;
import com.duluthtechnologies.ocpi.model.v211.Connector.PowerType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents a Connector object according to the OCPI 2.1.1 standard.
 */
public record Connector(

		/**
		 * Identifier of the connector within the EVSE. Two connectors may have the same
		 * id as long as they do not belong to the same EVSE object.
		 */
		@NotNull @Size(max = 36) String id,

		/**
		 * The standard of the installed connector.
		 */
		@NotNull ConnectorType standard,

		/**
		 * The format (socket/cable) of the installed connector.
		 */
		@NotNull ConnectorFormat format,

		/**
		 * The power type of the connector.
		 */
		@JsonProperty("power_type") @NotNull PowerType powerType,

		/**
		 * Voltage of the connector (line to neutral for AC_3_PHASE), in volt [V].
		 */
		@NotNull Integer voltage,

		/**
		 * Maximum amperage of the connector, in ampere [A].
		 */
		@NotNull Integer amperage,

		/**
		 * Identifier of the current charging tariff structure. For a “Free of Charge”
		 * tariff this field should be set, and point to a defined “Free of Charge”
		 * tariff.
		 */
		@JsonProperty("tariff_id") @Size(max = 36) String tariffId,

		/**
		 * URL to the operator’s terms and conditions.
		 */
		@JsonProperty("terms_and_conditions") @Size(max = 255) String termsAndConditions,

		/**
		 * Timestamp when this Connectors was last updated (or created).
		 */
		@JsonProperty("last_updated") @NotNull @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC") Instant lastUpdated) {

	/**
	 * Represents connector types according to the OCPI 2.1.1 standard.
	 */
	public enum ConnectorType {

		/**
		 * The connector type is CHAdeMO, DC
		 */
		CHADEMO,

		/**
		 * Standard/Domestic household, type “A”, NEMA 1-15, 2 pins
		 */
		DOMESTIC_A,

		/**
		 * Standard/Domestic household, type “B”, NEMA 5-15, 3 pins
		 */
		DOMESTIC_B,

		/**
		 * Standard/Domestic household, type “C”, CEE 7/17, 2 pins
		 */
		DOMESTIC_C,

		/**
		 * Standard/Domestic household, type “D”, 3 pin
		 */
		DOMESTIC_D,

		/**
		 * Standard/Domestic household, type “E”, CEE 7/5 3 pins
		 */
		DOMESTIC_E,

		/**
		 * Standard/Domestic household, type “F”, CEE 7/4, Schuko, 3 pins
		 */
		DOMESTIC_F,

		/**
		 * Standard/Domestic household, type “G”, BS 1363, Commonwealth, 3 pins
		 */
		DOMESTIC_G,

		/**
		 * Standard/Domestic household, type “H”, SI-32, 3 pins
		 */
		DOMESTIC_H,

		/**
		 * Standard/Domestic household, type “I”, AS 3112, 3 pins
		 */
		DOMESTIC_I,

		/**
		 * Standard/Domestic household, type “J”, SEV 1011, 3 pins
		 */
		DOMESTIC_J,

		/**
		 * Standard/Domestic household, type “K”, DS 60884-2-D1, 3 pins
		 */
		DOMESTIC_K,

		/**
		 * Standard/Domestic household, type “L”, CEI 23-16-VII, 3 pins
		 */
		DOMESTIC_L,

		/**
		 * IEC 60309-2 Industrial Connector single phase 16 Amperes (usually blue)
		 */
		IEC_60309_2_SINGLE_16,

		/**
		 * IEC 60309-2 Industrial Connector three phase 16 Amperes (usually red)
		 */
		IEC_60309_2_THREE_16,

		/**
		 * IEC 60309-2 Industrial Connector three phase 32 Amperes (usually red)
		 */
		IEC_60309_2_THREE_32,

		/**
		 * IEC 60309-2 Industrial Connector three phase 64 Amperes (usually red)
		 */
		IEC_60309_2_THREE_64,

		/**
		 * IEC 62196 Type 1 “SAE J1772”
		 */
		IEC_62196_T1,

		/**
		 * Combo Type 1 based, DC
		 */
		IEC_62196_T1_COMBO,

		/**
		 * IEC 62196 Type 2 “Mennekes”
		 */
		IEC_62196_T2,

		/**
		 * Combo Type 2 based, DC
		 */
		IEC_62196_T2_COMBO,

		/**
		 * IEC 62196 Type 3A
		 */
		IEC_62196_T3A,

		/**
		 * IEC 62196 Type 3C “Scame”
		 */
		IEC_62196_T3C,

		/**
		 * Tesla Connector “Roadster”-type (round, 4 pin)
		 */
		TESLA_R,

		/**
		 * Tesla Connector “Model-S”-type (oval, 5 pin)
		 */
		TESLA_S;
	}

	/**
	 * Represents connector formats according to the OCPI 2.1.1 standard.
	 */
	public enum ConnectorFormat {

		/**
		 * The connector is a socket; the EV user needs to bring a fitting plug.
		 */
		SOCKET,

		/**
		 * The connector is an attached cable; the EV users car needs to have a fitting
		 * inlet.
		 */
		CABLE;
	}

	/**
	 * Represents power types according to the OCPI 2.1.1 standard.
	 */
	public enum PowerType {

		/**
		 * AC mono phase.
		 */
		AC_1_PHASE,

		/**
		 * AC 3 phase.
		 */
		AC_3_PHASE,

		/**
		 * Direct Current.
		 */
		DC;
	}

}
