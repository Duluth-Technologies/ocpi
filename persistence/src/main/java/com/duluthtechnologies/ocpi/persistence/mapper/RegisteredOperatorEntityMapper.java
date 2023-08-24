package com.duluthtechnologies.ocpi.persistence.mapper;

import java.time.Instant;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;
import org.slf4j.LoggerFactory;

import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;
import com.duluthtechnologies.ocpi.core.model.RegisteredOperator;
import com.duluthtechnologies.ocpi.core.model.v211.RegisteredCPOV211;
import com.duluthtechnologies.ocpi.core.model.v211.RegisteredEMSPV211;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredCPOEntity;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredEMSPEntity;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredOperatorEntity;
import com.duluthtechnologies.ocpi.persistence.entity.v211.RegisteredCPOV211Entity;
import com.duluthtechnologies.ocpi.persistence.entity.v211.RegisteredEMSPV211Entity;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public interface RegisteredOperatorEntityMapper {

	@SubclassMapping(source = RegisteredCPOV211.class, target = RegisteredCPOV211Entity.class)
	@SubclassMapping(source = RegisteredCPO.class, target = RegisteredCPOEntity.class)
	@SubclassMapping(source = RegisteredEMSPV211.class, target = RegisteredEMSPV211Entity.class)
	@SubclassMapping(source = RegisteredEMSP.class, target = RegisteredEMSPEntity.class)
	RegisteredOperatorEntity toEntity(RegisteredOperator model);

	RegisteredCPOV211Entity toEntity(RegisteredCPOV211 registeredCPOV211, long id, Instant createdDate);

	RegisteredCPOEntity toEntity(RegisteredCPO registeredCPO, long id, Instant createdDate);

	RegisteredEMSPV211Entity toEntity(RegisteredEMSPV211 registeredEMSPV211, long id, Instant createdDate);

	RegisteredEMSPEntity toEntity(RegisteredEMSP registeredEMSP, long id, Instant createdDate);

	default void updateEntity(@MappingTarget RegisteredOperatorEntity entity, RegisteredOperator model) {
		if (model instanceof RegisteredCPOV211 registeredCPOV211
				&& entity instanceof RegisteredCPOV211Entity registeredCPOV211Entity) {
			updateRegisteredCPOV211Entity(registeredCPOV211Entity, registeredCPOV211);
		} else if (model instanceof RegisteredCPO registeredCPO
				&& entity instanceof RegisteredCPOEntity registeredCPOEntity) {
			updateRegisteredCPOEntity(registeredCPOEntity, registeredCPO);
		} else if (model instanceof RegisteredEMSPV211 registeredEMSPV211
				&& entity instanceof RegisteredEMSPV211Entity registeredEMSPV211Entity) {
			updateRegisteredEMSPV211Entity(registeredEMSPV211Entity, registeredEMSPV211);
		} else if (model instanceof RegisteredEMSP registeredEMSP
				&& entity instanceof RegisteredEMSPEntity registeredEMSPEntity) {
			updateRegisteredEMSPEntity(registeredEMSPEntity, registeredEMSP);
		} else {
			String message = "Unsupported model type " + model.getClass().getSimpleName() + " or entity type "
					+ entity.getClass().getSimpleName();
			LoggerFactory.getLogger(RegisteredOperatorEntityMapper.class).error(message);
			throw new IllegalArgumentException(message);
		}
	}

	void updateRegisteredCPOV211Entity(@MappingTarget RegisteredCPOV211Entity entity, RegisteredCPOV211 model);

	void updateRegisteredCPOEntity(@MappingTarget RegisteredCPOEntity entity, RegisteredCPO model);

	void updateRegisteredEMSPV211Entity(@MappingTarget RegisteredEMSPV211Entity entity, RegisteredEMSPV211 model);

	void updateRegisteredEMSPEntity(@MappingTarget RegisteredEMSPEntity entity, RegisteredEMSP model);

}
