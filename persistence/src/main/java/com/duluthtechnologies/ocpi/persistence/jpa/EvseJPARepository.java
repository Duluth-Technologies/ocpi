package com.duluthtechnologies.ocpi.persistence.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import com.duluthtechnologies.ocpi.persistence.entity.EvseEntity;

@Repository
public interface EvseJPARepository extends JpaRepository<EvseEntity, Long>, RevisionRepository<EvseEntity, Long, Long> {

	Optional<EvseEntity> findByKeyAndDeleted(String evseKey, boolean deleted);

	default Optional<EvseEntity> findByKey(String evseKey) {
		return findByKeyAndDeleted(evseKey, false);
	}

	@Query(value = "SELECT e.* FROM evses e JOIN locations l ON e.location_id = l.id JOIN registered_cpo_locations c ON l.id = c.id JOIN registered_operators r ON r.id = c.registered_cpo_id WHERE r.country_code = ?1 and r.party_id = ?2 and l.ocpi_id = ?3 and e.ocpi_id = ?4 and e.deleted = ?5", nativeQuery = true)
	Optional<EvseEntity> findByCPOLocationRegisteredOperatorCountryCodeAndCPOLocationRegisteredOperatorPartyIdAndCPOLocationOcpiIdAndOcpiIdAndDeleted(
			String countryCode, String partyId, String locationOcpiId, String evseOcpiId, boolean deleted);

	default Optional<EvseEntity> findByCPOLocationRegisteredOperatorCountryCodeAndCPOLocationRegisteredOperatorPartyIdAndCPOLocationOcpiIdAndOcpiId(
			String countryCode, String partyId, String locationOcpiId, String evseOcpiId) {
		return findByCPOLocationRegisteredOperatorCountryCodeAndCPOLocationRegisteredOperatorPartyIdAndCPOLocationOcpiIdAndOcpiIdAndDeleted(
				countryCode, partyId, locationOcpiId, evseOcpiId, false);
	}

}
