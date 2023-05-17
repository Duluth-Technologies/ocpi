package com.duluthtechnologies.ocpi.core.store;

import java.util.Optional;

import com.duluthtechnologies.ocpi.core.model.RegisteredOperator;

import jakarta.validation.constraints.NotEmpty;

public interface RegisteredOperatorStore {

	RegisteredOperator create(RegisteredOperator registeredOperator);

	RegisteredOperator update(RegisteredOperator registeredOperator);

	RegisteredOperator updateNow(RegisteredOperator registeredOperator);

	Optional<RegisteredOperator> findByIncomingToken(String token);

	Optional<RegisteredOperator> findByKey(@NotEmpty String key);

}
