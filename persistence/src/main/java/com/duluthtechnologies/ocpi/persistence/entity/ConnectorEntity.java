package com.duluthtechnologies.ocpi.persistence.entity;

import java.time.Instant;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Evse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "connectors")
@EntityListeners(AuditingEntityListener.class)
@Audited
public class ConnectorEntity implements Connector {

	@Id
	@GeneratedValue
	@SequenceGenerator(name = "connectors_generator", sequenceName = "connectors_seq", allocationSize = 50)
	private long id;

	@Version
	@Column(name = "version")
	Long version;

	@Column(name = "deleted")
	private boolean deleted;

	@Column(name = "key")
	private String key;

	@Column(name = "connector_id")
	private String connectorId;

	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private Type type;

	@Enumerated(EnumType.STRING)
	@Column(name = "format")
	private Format format;

	@Enumerated(EnumType.STRING)
	@Column(name = "power_type")
	private PowerType powerType;

	@Column(name = "maximum_voltage")
	private Integer maximumVoltage;

	@Column(name = "maximum_amperage")
	private Integer maximumAmperage;

	@Column(name = "created_date", updatable = false)
	@CreatedDate
	private Instant createdDate;

	@Column(name = "last_modified_date")
	@LastModifiedDate
	private Instant lastModifiedDate;

	@ManyToOne(targetEntity = EvseEntity.class)
	@JoinColumn(name = "evse_id")
	private Evse evse;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getConnectorId() {
		return connectorId;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Format getFormat() {
		return format;
	}

	@Override
	public Integer getMaximumVoltage() {
		return maximumVoltage;
	}

	@Override
	public Integer getMaximumAmperage() {
		return maximumAmperage;
	}

	@Override
	public Instant getLastModifiedDate() {
		return lastModifiedDate;
	}

	@Override
	public Evse getEvse() {
		return evse;
	}

	public Instant getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Instant createdDate) {
		this.createdDate = createdDate;
	}

	public void setConnectorId(String connectorId) {
		this.connectorId = connectorId;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public void setMaximumVoltage(Integer maximumVoltage) {
		this.maximumVoltage = maximumVoltage;
	}

	public void setMaximumAmperage(Integer maximumAmperage) {
		this.maximumAmperage = maximumAmperage;
	}

	public void setEvse(Evse evse) {
		this.evse = evse;
	}

	public PowerType getPowerType() {
		return powerType;
	}

	public void setPowerType(PowerType powerType) {
		this.powerType = powerType;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
