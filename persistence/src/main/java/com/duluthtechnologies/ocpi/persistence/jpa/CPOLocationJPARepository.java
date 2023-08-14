package com.duluthtechnologies.ocpi.persistence.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.duluthtechnologies.ocpi.persistence.entity.CPOLocationEntity;

@Repository
public interface CPOLocationJPARepository extends JpaRepository<CPOLocationEntity, Long> {

	@Query("SELECT l FROM CPOLocationEntity l WHERE l.registeredCPO.key = :registeredCPOKey AND l.ocpiId = :ocpiId")
	Optional<CPOLocationEntity> findByRegisteredCPOKeyAndOcpiId(@Param("registeredCPOKey") String registeredCPOKey,
			@Param("ocpiId") String ocpiId);

}
