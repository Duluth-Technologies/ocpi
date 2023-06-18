package com.duluthtechnologies.ocpi.persistence.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import com.duluthtechnologies.ocpi.persistence.entity.ConnectorEntity;

@Repository
public interface ConnectorJPARepository
		extends JpaRepository<ConnectorEntity, Long>, RevisionRepository<ConnectorEntity, Long, Long> {

	default Optional<ConnectorEntity> findByKey(String key) {
		return findByKeyAndDeleted(key, false);
	}

	Optional<ConnectorEntity> findByKeyAndDeleted(String key, boolean deleted);

}
