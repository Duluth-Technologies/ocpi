package com.duluthtechnologies.ocpi.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;

import com.duluthtechnologies.ocpi.api.dto.EvseView;
import com.duluthtechnologies.ocpi.core.model.Evse;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, uses = ConnectorDTOMapper.class)
public interface EvseDTOMapper {

	EvseView toEvseView(Evse evse);
}
