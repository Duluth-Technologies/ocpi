package com.duluthtechnologies.ocpi.api.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class LocationCreationForm {

	@Size(max = 255)
	private String key;

	@NotEmpty
	@Size(max = 255)
	private String name;

	@NotEmpty
	@Size(max = 45)
	private String address;

	@NotEmpty
	@Size(max = 45)
	private String city;

	@NotEmpty
	@Size(max = 10)
	private String zipCode;

	@NotEmpty
	@Size(min = 3, max = 3)
	private String countryCode;

	@Min(-90)
	@Max(90)
	private double latitude;

	@Min(-180)
	@Max(180)
	private double longitude;

	@Valid
	private List<EvseCreationForm> evses;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public List<EvseCreationForm> getEvses() {
		return evses;
	}

	public void setEvses(List<EvseCreationForm> evses) {
		this.evses = evses;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
