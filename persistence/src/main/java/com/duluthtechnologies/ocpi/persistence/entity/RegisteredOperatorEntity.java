package com.duluthtechnologies.ocpi.persistence.entity;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.duluthtechnologies.ocpi.core.model.RegisteredOperator;
import com.duluthtechnologies.ocpi.persistence.helper.TokenEncryptor;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "registered_operators")
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class RegisteredOperatorEntity implements RegisteredOperator {

	@Id
	@GeneratedValue
	@SequenceGenerator(name = "registered_operators_generator", sequenceName = "registered_operators_seq", allocationSize = 50)
	private long id;

	@Column(name = "key")
	private String key;

	@Column(name = "version_url")
	private String versionUrl;

	@Column(name = "created_date", updatable = false)
	@CreatedDate
	private Instant createdDate;

	@Column(name = "last_modified_date")
	@LastModifiedDate
	private Instant lastModifiedDate;

	@Column(name = "incoming_token")
	@Convert(converter = TokenEncryptor.class)
	private String incomingToken;

	@Column(name = "outgoing_token")
	@Convert(converter = TokenEncryptor.class)
	private String outgoingToken;

	@Column(name = "party_id")
	private String partyId;

	@Column(name = "country_code")
	private String countryCode;

	@Column(name = "name")
	private String name;

	@Column(name = "logo_url")
	private String logoUrl;

	@Column(name = "logo_thumbnail_url")
	private String logoThumbnailUrl;

	@Column(name = "website_url")
	private String websiteUrl;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public @NotEmpty String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getVersionUrl() {
		return versionUrl;
	}

	public void setVersionUrl(String versionUrl) {
		this.versionUrl = versionUrl;
	}

	@Override
	public String getIncomingToken() {
		return incomingToken;
	}

	@Override
	public String getOutgoingToken() {
		return outgoingToken;
	}

	@Override
	public String getPartyId() {
		return partyId;
	}

	@Override
	public String getCountryCode() {
		return countryCode;
	}

	@Override
	public @NotEmpty String getName() {
		return name;
	}

	@Override
	public String getLogoUrl() {
		return logoUrl;
	}

	@Override
	public String getLogoThumbnailUrl() {
		return logoThumbnailUrl;
	}

	@Override
	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setIncomingToken(String incomingToken) {
		this.incomingToken = incomingToken;
	}

	public void setOutgoingToken(String outgoingToken) {
		this.outgoingToken = outgoingToken;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public void setLogoThumbnailUrl(String logoThumbnailUrl) {
		this.logoThumbnailUrl = logoThumbnailUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public Instant getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Instant createdDate) {
		this.createdDate = createdDate;
	}

}
