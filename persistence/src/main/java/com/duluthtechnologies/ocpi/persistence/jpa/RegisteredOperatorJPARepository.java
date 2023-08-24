package com.duluthtechnologies.ocpi.persistence.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.duluthtechnologies.ocpi.persistence.entity.RegisteredOperatorEntity;

import jakarta.validation.constraints.NotEmpty;

@Repository
public interface RegisteredOperatorJPARepository extends JpaRepository<RegisteredOperatorEntity, Long> {

	Optional<RegisteredOperatorEntity> findByKey(@NotEmpty String key);

	Optional<RegisteredOperatorEntity> findByIncomingToken(@NotEmpty String token);

	Optional<RegisteredOperatorEntity> findByCountryCodeAndPartyId(String countryCode, String partyId);

	@Transactional
	@Modifying
	// Need to do that manually
	@Query(value = "INSERT INTO registered_emsps_v211 (id) VALUES (:id)", nativeQuery = true)
	void upgradeEMSPToV211(@Param("id") Long registeredOperatorEntityId);

	@Transactional
	@Modifying
	// Need to do that manually
	@Query(value = "INSERT INTO registered_cpos_v211 (id) VALUES (:id)", nativeQuery = true)
	void upgradeCPOToV211(@Param("id") Long registeredOperatorEntityId);
}
