package com.duluthtechnologies.ocpi.persistence.entity;

import org.hibernate.envers.NotAudited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPOLocation;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "registered_cpo_locations")
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
public class CPOLocationEntity extends LocationEntity implements RegisteredCPOLocation {

	@ManyToOne(targetEntity = RegisteredCPOEntity.class)
	@JoinColumn(name = "registered_cpo_id")
	@NotAudited
	private RegisteredCPO registeredCPO;

	@Override
	public RegisteredCPO getRegisteredCPO() {
		return registeredCPO;
	}

	public void setRegisteredCPO(RegisteredCPOEntity registeredCPO) {
		this.registeredCPO = registeredCPO;
	}

}
