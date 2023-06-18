package com.duluthtechnologies.ocpi.persistence.entity;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.duluthtechnologies.ocpi.core.model.Evse;
import com.duluthtechnologies.ocpi.core.model.Location;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "locations")
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
@Audited
public class LocationEntity implements Location {

	@Id
	@GeneratedValue
	@SequenceGenerator(name = "locations_generator", sequenceName = "locations_seq", allocationSize = 50)
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

	@Column(name = "name")
	private String name;

	@Column(name = "address")
	private String address;

	@Column(name = "city")
	private String city;

	@Column(name = "zip_code")
	private String zipCode;

	@Column(name = "country_code")
	private String countryCode;

	@Column(name = "latitude")
	private double latitude;

	@Column(name = "longitude")
	private double longitude;

	@Column(name = "created_date", updatable = false)
	@CreatedDate
	private Instant createdDate;

	@Column(name = "last_modified_date")
	@LastModifiedDate
	private Instant lastModifiedDate;

	@OneToMany(targetEntity = EvseEntity.class)
	@JoinColumn(name = "location_id")
	@Where(clause = "deleted = false")
	private List<Evse> evses;

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
	public String getName() {
		return name;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public String getCity() {
		return city;
	}

	@Override
	public String getZipCode() {
		return zipCode;
	}

	@Override
	public String getCountryCode() {
		return countryCode;
	}

	@Override
	public double getLatitude() {
		return latitude;
	}

	@Override
	public double getLongitude() {
		return longitude;
	}

	@Override
	public Instant getLastModifiedDate() {
		return lastModifiedDate;
	}

	@Override
	public List<Evse> getEvses() {
		return evses;
	}

	public Instant getCreatedDate() {
		return createdDate;
	}

	public void setEvses(List<Evse> evses) {
		this.evses = evses;
	}

	public void setOcpiId(String ocpiId) {
		this.ocpiId = ocpiId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
