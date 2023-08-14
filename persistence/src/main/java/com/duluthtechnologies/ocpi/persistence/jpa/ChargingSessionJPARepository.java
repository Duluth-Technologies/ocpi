package com.duluthtechnologies.ocpi.persistence.jpa;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.duluthtechnologies.ocpi.persistence.entity.ChargingSessionEntity;
import com.duluthtechnologies.ocpi.persistence.entity.LocationEntity;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredEMSPChargingSessionEntity;

@Repository
public interface ChargingSessionJPARepository extends JpaRepository<ChargingSessionEntity, Long>,
		RevisionRepository<ChargingSessionEntity, Long, Long>, JpaSpecificationExecutor<LocationEntity> {

	List<RegisteredEMSPChargingSessionEntity> findByRegisteredEMSPKeyAndLastModifiedDateBetween(String key,
			Instant startDate, Instant endDate, Pageable pageable);

	List<ChargingSessionEntity> findByConnectorKeyAndLastModifiedDateBetween(String key, Instant startDate,
			Instant endDate);

	List<ChargingSessionEntity> findByLastModifiedDateBetween(Instant startDate, Instant endDate);

	@Query("SELECT cs FROM ChargingSessionEntity cs JOIN cs.connector c JOIN c.evse e JOIN e.location l WHERE TYPE(l) = CPOLocationEntity AND l.registeredCPO.key = :registeredCPOKey AND cs.ocpiId = :ocpiId")
	Optional<ChargingSessionEntity> findByRegisteredCPOKeyAndOcpiId(@Param("registeredCPOKey") String registeredCPOKey,
			@Param("ocpiId") String ocpiId);

	Optional<ChargingSessionEntity> findByKey(String chargingSessionKey);
}
