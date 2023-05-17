package com.duluthtechnologies.ocpi.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

import com.duluthtechnologies.ocpi.api.dto.CPORegistrationForm;
import com.duluthtechnologies.ocpi.api.dto.EMSPRegistrationForm;
import com.duluthtechnologies.ocpi.api.dto.RegisteredCPOView;
import com.duluthtechnologies.ocpi.api.dto.RegisteredEMSPView;
import com.duluthtechnologies.ocpi.api.dto.v211.RegisteredCPOV211View;
import com.duluthtechnologies.ocpi.api.dto.v211.RegisteredEMSPV211View;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;
import com.duluthtechnologies.ocpi.core.model.v211.RegisteredCPOV211;
import com.duluthtechnologies.ocpi.core.model.v211.RegisteredEMSPV211;
import com.duluthtechnologies.ocpi.core.service.RegisteredOperatorService.RegisteredCPOCreationForm;
import com.duluthtechnologies.ocpi.core.service.RegisteredOperatorService.RegisteredEMSPCreationForm;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public interface RegisteredOperatorDTOMapper {

	RegisteredCPOCreationForm toRegisteredCPOCreationForm(CPORegistrationForm dto);

	RegisteredEMSPCreationForm toRegisteredEMSPCreationForm(EMSPRegistrationForm dto);

	@SubclassMapping(source = RegisteredCPOV211.class, target = RegisteredCPOV211View.class)
	@SubclassMapping(source = RegisteredCPO.class, target = RegisteredCPOView.class)
	RegisteredCPOView toRegisteredCPOView(RegisteredCPO model);

	@SubclassMapping(source = RegisteredEMSPV211.class, target = RegisteredEMSPV211View.class)
	@SubclassMapping(source = RegisteredEMSP.class, target = RegisteredEMSPView.class)
	RegisteredEMSPView toRegisteredEMSPView(RegisteredEMSP model);

}
