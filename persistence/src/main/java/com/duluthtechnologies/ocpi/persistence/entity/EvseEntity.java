package com.duluthtechnologies.ocpi.persistence.entity;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Evse;
import com.duluthtechnologies.ocpi.core.model.Location;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "evses")
@EntityListeners(AuditingEntityListener.class)
@Audited
public class EvseEntity implements Evse {

	@Id
	@GeneratedValue
	@SequenceGenerator(name = "evses_generator", sequenceName = "evses_seq", allocationSize = 50)
	private long id;

	@Version
	@Column(name = "version")
	Long version;

	@Column(name = "deleted")
	private boolean deleted;

	@Column(name = "key")
	private String key;

	@Column(name = "ocpi_id")
	private String ocpiId;

	@Column(name = "evse_id")
	private String evseId;

	@ManyToOne(targetEntity = LocationEntity.class)
	@JoinColumn(name = "location_id")
	private Location location;

	@OneToMany(targetEntity = ConnectorEntity.class)
	@JoinColumn(name = "evse_id")
	@Where(clause = "deleted = false")
	private List<Connector> connectors;

	@Column(name = "created_date", updatable = false)
	@CreatedDate
	private Instant createdDate;

	@Column(name = "last_modified_date")
	@LastModifiedDate
	private Instant lastModifiedDate;

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
	public String getOcpiId() {
		return ocpiId;
	}

	@Override
	public String getEvseId() {
		return evseId;
	}

	@Override
	public Instant getLastModifiedDate() {
		return lastModifiedDate;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public List<Connector> getConnectors() {
		return connectors;
	}

	public Instant getCreatedDate() {
		return createdDate;
	}

	public void setOcpiId(String ocpiId) {
		this.ocpiId = ocpiId;
	}

	public void setEvseId(String evseId) {
		this.evseId = evseId;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setConnectors(List<Connector> connectors) {
		this.connectors = connectors;
	}

}
