package com.duluthtechnologies.ocpi.core.store;

import java.util.List;
import java.util.Optional;

import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;
import com.duluthtechnologies.ocpi.core.model.RegisteredOperator;

import jakarta.validation.constraints.NotEmpty;

public interface RegisteredOperatorStore {

	RegisteredOperator create(RegisteredOperator registeredOperator);

	RegisteredOperator update(RegisteredOperator registeredOperator);

	Optional<RegisteredOperator> findByIncomingToken(String token);

	Optional<RegisteredOperator> findByKey(@NotEmpty String key);

	List<RegisteredCPO> findCPOs();

	List<RegisteredEMSP> findEMSPs();

}
