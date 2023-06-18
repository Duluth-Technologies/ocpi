package com.duluthtechnologies.ocpi.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import com.duluthtechnologies.ocpi.core.model.Location;
import com.duluthtechnologies.ocpi.persistence.entity.CPOLocationEntity;
import com.duluthtechnologies.ocpi.persistence.entity.LocationEntity;

@Repository
public interface LocationJPARepository extends JpaRepository<LocationEntity, Long>,
		RevisionRepository<LocationEntity, Long, Long>, JpaSpecificationExecutor<LocationEntity> {

	Optional<LocationEntity> findByKeyAndDeleted(String key, boolean deleted);

	default Optional<LocationEntity> findByKey(String key) {
		return findByKeyAndDeleted(key, false);
	}

	@Query(value = "SELECT l.*, c.registered_cpo_id FROM locations AS l INNER JOIN registered_cpo_locations AS c ON l.id = c.id INNER JOIN registered_operators AS r ON r.id = c.registered_cpo_id WHERE r.country_code = ?1 AND r.party_id = ?2 AND l.ocpi_id = ?3 AND l.deleted = ?4", nativeQuery = true)
	Optional<CPOLocationEntity> findByRegisteredCpoCountryCodeAndRegisteredCpoPartyIdAndOcpiIdAndDeleted(
			String countryCode, String partyId, String id, boolean deleted);

	default Optional<CPOLocationEntity> findByRegisteredCpoCountryCodeAndRegisteredCpoPartyIdAndOcpiId(
			String countryCode, String partyId, String id) {
		return findByRegisteredCpoCountryCodeAndRegisteredCpoPartyIdAndOcpiIdAndDeleted(countryCode, partyId, id,
				false);
	}

	@Query(value = "SELECT l.*, c.registered_cpo_id FROM locations AS l INNER JOIN registered_cpo_locations AS c ON l.id = c.id INNER JOIN registered_operators AS r ON r.id = c.registered_cpo_id WHERE r.key = ?1 AND l.deleted = ?2", nativeQuery = true)
	List<CPOLocationEntity> findByRegisteredCpoKeyAndDeleted(String key, boolean deleted);

	default List<CPOLocationEntity> findByRegisteredCpoKey(String key) {
		return findByRegisteredCpoKeyAndDeleted(key, false);
	}

	List<Location> findByOcpiIdAndDeleted(String ocpiId, boolean deleted);

	default List<Location> findByOcpiId(String ocpiId) {
		return findByOcpiIdAndDeleted(ocpiId, false);
	}

	@Query(value = "SELECT l.*, c.registered_cpo_id FROM locations AS l INNER JOIN registered_cpo_locations AS c ON l.id = c.id INNER JOIN registered_operators AS r ON r.id = c.registered_cpo_id WHERE r.country_code = ?1 AND r.party_id = ?2 AND l.deleted = ?3", nativeQuery = true)
	List<LocationEntity> findCpoLocationByCountryCodeAndPartyIdAndDeleted(String countryCode, String partyId,
			boolean deleted);

	default List<LocationEntity> findCpoLocationByCountryCodeAndPartyId(String countryCode, String partyId) {
		return findCpoLocationByCountryCodeAndPartyIdAndDeleted(countryCode, partyId, false);
	}

}
