package com.duluthtechnologies.ocpi.persistence.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duluthtechnologies.ocpi.persistence.entity.RegisteredOperatorEntity;

import jakarta.validation.constraints.NotEmpty;

@Repository
public interface RegisteredOperatorJPARepository extends JpaRepository<RegisteredOperatorEntity, Long> {

	Optional<RegisteredOperatorEntity> findByKey(@NotEmpty String key);

	Optional<RegisteredOperatorEntity> findByIncomingToken(@NotEmpty String token);

	Optional<RegisteredOperatorEntity> findByCountryCodeAndPartyId(String countryCode, String partyId);

}
