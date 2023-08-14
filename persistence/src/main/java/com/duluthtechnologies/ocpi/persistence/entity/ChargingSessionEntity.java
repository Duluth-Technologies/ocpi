package com.duluthtechnologies.ocpi.persistence.entity;

import java.time.Instant;
import java.util.Currency;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.duluthtechnologies.ocpi.core.model.ChargingSession;
import com.duluthtechnologies.ocpi.core.model.Connector;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "charging_sessions")
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
@Audited
public class ChargingSessionEntity implements ChargingSession {

	@Id
	@GeneratedValue
	@SequenceGenerator(name = "charging_sessions_generator", sequenceName = "charging_sessions_seq", allocationSize = 50)
	private long id;

	@Version
	@Column(name = "version")
	Long version;

	@Column(name = "key")
	private String key;

	@Column(name = "ocpi_id")
	private String ocpiId;

	@Column(name = "created_date", updatable = false)
	@CreatedDate
	private Instant createdDate;

	@Column(name = "last_modified_date")
	@LastModifiedDate
	private Instant lastModifiedDate;

	@Column(name = "start_date")
	private Instant startDate;

	@Column(name = "stop_date")
	private Instant stopDate;

	@Column(name = "disconnect_date")
	private Instant disconnectDate;

	@ManyToOne(targetEntity = ConnectorEntity.class)
	@JoinColumn(name = "connector_id")
	private Connector connector;

	@Column(name = "energy_delivered_in_wh")
	private Integer energyDeliveredInWh;

	@Column(name = "currency_code")
	private String currencyCode;

	@Column(name = "fractional_amount")
	private Integer fractionalAmount;

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getOcpiId() {
		return ocpiId;
	}

	@Override
	public Instant getCreatedDate() {
		return createdDate;
	}

	@Override
	public Instant getStartDate() {
		return startDate;
	}

	@Override
	public Instant getStopDate() {
		return stopDate;
	}

	@Override
	public Instant getDisconnectDate() {
		return disconnectDate;
	}

	@Override
	public Connector getConnector() {
		return connector;
	}

	@Override
	public Integer getEnergyDeliveredInWh() {
		return energyDeliveredInWh;
	}

	@Override
	public Instant getLastModifiedDate() {
		return lastModifiedDate;
	}

	@Override
	public Cost getCost() {
		if (currencyCode == null) {
			return null;
		} else {
			return new CostImpl(Currency.getInstance(currencyCode), fractionalAmount);
		}
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setOcpiId(String ocpiId) {
		this.ocpiId = ocpiId;
	}

	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}

	public void setStopDate(Instant stopDate) {
		this.stopDate = stopDate;
	}

	public void setDisconnectDate(Instant disconnectDate) {
		this.disconnectDate = disconnectDate;
	}

	public void setConnector(Connector connector) {
		this.connector = connector;
	}

	public void setEnergyDeliveredInWh(Integer energyDeliveredInWh) {
		this.energyDeliveredInWh = energyDeliveredInWh;
	}

	public void setCost(Cost cost) {
		if (cost == null) {
			this.fractionalAmount = null;
			this.currencyCode = null;
		} else {
			this.fractionalAmount = cost.getFractionalAmount();
			this.currencyCode = cost.getCurrency().getCurrencyCode();
		}
	}

	private static final record CostImpl(Currency currency, Integer fractionalAmount) implements Cost {

		@Override
		public Integer getFractionalAmount() {
			return fractionalAmount;
		}

		@Override
		public Currency getCurrency() {
			return currency;
		}

	}

}
