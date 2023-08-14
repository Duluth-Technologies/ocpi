package com.duluthtechnologies.ocpi.api.mapper;

import java.util.Currency;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

import com.duluthtechnologies.ocpi.api.dto.ChargingSessionView;
import com.duluthtechnologies.ocpi.api.dto.RegisteredEMSPChargingSessionForm;
import com.duluthtechnologies.ocpi.api.dto.RegisteredEMSPChargingSessionView;
import com.duluthtechnologies.ocpi.core.model.ChargingSession;
import com.duluthtechnologies.ocpi.core.model.ChargingSession.Cost;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSPChargingSession;
import com.duluthtechnologies.ocpi.core.service.ChargingSessionService.RegisteredEMSPChargingSessionCreationForm;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public interface ChargingSessionDTOMapper {

	RegisteredEMSPChargingSessionCreationForm toRegisteredEMSPChargingSessionCreationForm(
			RegisteredEMSPChargingSessionForm registeredEMSPChargingSessionForm);

	@Mapping(target = "connectorKey", source = "registeredEMSPChargingSession.connector.key")
	@Mapping(target = "registeredEmspKey", source = "registeredEMSP.key")
	RegisteredEMSPChargingSessionView toRegisteredEMSPChargingSessionView(
			RegisteredEMSPChargingSession registeredEMSPChargingSession);

	@Mapping(target = "connectorKey", source = "chargingSession.connector.key")
	@SubclassMapping(source = RegisteredEMSPChargingSession.class, target = RegisteredEMSPChargingSessionView.class)
	ChargingSessionView toChargingSessionView(ChargingSession chargingSession);

	com.duluthtechnologies.ocpi.core.service.ChargingSessionService.RegisteredEMSPChargingSessionForm toRegisteredEMSPChargingSessionForm(
			RegisteredEMSPChargingSessionForm registeredEMSPChargingSessionForm);

	default Cost toCost(RegisteredEMSPChargingSessionForm.Cost cost) {
		if (cost == null) {
			return null;
		}
		return new CostImpl(cost.getFractionalAmount(), cost.getCurrency());
	}

	public final class CostImpl implements Cost {

		private Integer fractionalAmount;
		private Currency currency;

		public CostImpl(Integer fractionalAmount, Currency currency) {
			super();
			this.fractionalAmount = fractionalAmount;
			this.currency = currency;
		}

		@Override
		public Integer getFractionalAmount() {
			return fractionalAmount;
		}

		@Override
		public Currency getCurrency() {
			return currency;
		}
	}
}
