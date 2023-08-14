package com.duluthtechnologies.ocpi.persistence.entity;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSPChargingSession;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "registered_emsp_charging_sessions")
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
@Audited
public class RegisteredEMSPChargingSessionEntity extends ChargingSessionEntity
		implements RegisteredEMSPChargingSession {

	@ManyToOne(targetEntity = RegisteredEMSPEntity.class)
	@JoinColumn(name = "registered_emsp_id")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private RegisteredEMSP registeredEMSP;

	@Override
	public RegisteredEMSP getRegisteredEMSP() {
		return registeredEMSP;
	}

	public void setRegisteredEMSP(RegisteredEMSP registeredEMSP) {
		this.registeredEMSP = registeredEMSP;
	}

}
