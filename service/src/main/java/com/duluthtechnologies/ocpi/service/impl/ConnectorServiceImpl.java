package com.duluthtechnologies.ocpi.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.duluthtechnologies.ocpi.api.client.OcpiApiClient;
import com.duluthtechnologies.ocpi.core.configuration.CPOInfo;
import com.duluthtechnologies.ocpi.core.model.Connector;
import com.duluthtechnologies.ocpi.core.model.Connector.Status;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPOLocation;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;
import com.duluthtechnologies.ocpi.core.model.v211.RegisteredCPOV211;
import com.duluthtechnologies.ocpi.core.model.v211.RegisteredEMSPV211;
import com.duluthtechnologies.ocpi.core.service.ConnectorService;
import com.duluthtechnologies.ocpi.core.service.LocationService.ConnectorForm;
import com.duluthtechnologies.ocpi.core.service.RegisteredOperatorService;
import com.duluthtechnologies.ocpi.core.store.ConnectorStore;
import com.duluthtechnologies.ocpi.model.Response;
import com.duluthtechnologies.ocpi.service.helper.KeyGenerator;
import com.duluthtechnologies.ocpi.service.mapper.ConnectorMapper;
import com.duluthtechnologies.ocpi.service.mapper.EvseMapper;
import com.duluthtechnologies.ocpi.service.model.impl.ConnectorImpl;

import jakarta.transaction.Transactional;

@Component
public class ConnectorServiceImpl implements ConnectorService {

	private static final Logger LOG = LoggerFactory.getLogger(ConnectorServiceImpl.class);

	private final RegisteredOperatorService registeredOperatorService;

	private final ConnectorStore connectorStore;

	private final ConnectorMapper connectorMapper;

	private final EvseMapper evseMapper;

	private final Optional<CPOInfo> cpoInfo;

	private final RestTemplate restTemplate;

	private final OcpiApiClient ocpiApiClient;

	private final TaskExecutor taskExecutor;

	public ConnectorServiceImpl(ConnectorStore connectorStore, ConnectorMapper connectorMapper, EvseMapper evseMapper,
			TaskExecutor taskExecutor, RegisteredOperatorService registeredOperatorService, Optional<CPOInfo> cpoInfo,
			OcpiApiClient ocpiApiClient) {
		super();
		this.registeredOperatorService = registeredOperatorService;
		this.connectorStore = connectorStore;
		this.connectorMapper = connectorMapper;
		this.evseMapper = evseMapper;
		this.cpoInfo = cpoInfo;
		this.restTemplate = new RestTemplate();
		this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		this.ocpiApiClient = ocpiApiClient;
		this.taskExecutor = taskExecutor;
	}

	@Override
	public Optional<Connector> find(String countryCode, String partyId, String locationId, String evseId,
			String connectorId) {
		return connectorStore.findByCountryCodeAndPartyIdAndLocationIdAndEvseId(countryCode, partyId, locationId,
				evseId);
	}

	@Override
	public Connector create(String evseKey, ConnectorForm connectorForm) {
		LOG.info("Creating Connector [{}] for EVSE with key [{}]...", connectorForm, evseKey);
		ConnectorImpl connectorImpl = connectorMapper.toConnector(connectorForm);
		connectorImpl.setKey(KeyGenerator.generateKey(Connector.class));
		return connectorStore.createConnector(evseKey, connectorImpl);
	}

	@Override
	public Connector update(String key, ConnectorForm connectorForm) {
		LOG.info("Updating Connector with key [{}]...", key);
		ConnectorImpl connectorImpl = connectorMapper.toConnector(connectorForm);
		return connectorStore.update(key, connectorImpl);
	}

	@Override
	public void patch(String key, ConnectorForm connectorForm) {
		LOG.info("Patching Connector with key [{}]...", key);
		ConnectorImpl connectorImpl = connectorMapper.toConnector(connectorForm);
		connectorStore.patch(key, connectorImpl);
	}

	@Override
	public void delete(String key) {
		LOG.info("Deleting Connector with key [{}]...", key);
		connectorStore.delete(key);
	}

	@Override
	public Connector refreshStatus(String key) {
		LOG.debug("Refreshing status of Connector with key [{}]...", key);
		try {
			Connector connector = getByKey(key);
			if (connector.getEvse().getLocation() instanceof RegisteredCPOLocation registeredCPOLocation) {
				RegisteredCPO registeredCPO = registeredCPOLocation.getRegisteredCPO();
				if (registeredCPO instanceof RegisteredCPOV211 registeredCPOV211) {
					com.duluthtechnologies.ocpi.model.v211.EVSE evseV211 = ocpiApiClient.getEvse211(
							registeredCPOV211.getOutgoingToken(), registeredCPOV211.getLocationsUrl(),
							registeredCPOV211.getCountryCode(), registeredCPOV211.getPartyId(),
							registeredCPOLocation.getOcpiId(), connector.getEvse().getOcpiId());
					Connector.Status updatedStatus = evseMapper.toStatus(evseV211);
					return connectorStore.updateStatus(key, updatedStatus);
				} else {
					String message = "Cannot refresh Connector with key [%s] as it belongs to a Registered CPO whose type [%s] is not supported"
							.formatted(key, registeredCPO.getClass().getSimpleName());
					LOG.error(message);
					throw new RuntimeException(message);
				}
			} else {
				String message = "Cannot refresh Connector with key [%s] as it doesn't belong to a Registered CPO"
						.formatted(key);
				LOG.error(message);
				throw new RuntimeException(message);
			}
		} catch (Exception e) {
			String message = "Exception caught while refreshing status of Connector with key [%s]".formatted(key);
			LOG.error(message);
			throw new RuntimeException(message);
		}
	}

	@Override
	public Connector getByKey(String key) {
		return connectorStore.findByKey(key).orElseThrow(() -> {
			String message = "Cannot get Connector with key [%s] as it doesn't exist.".formatted(key);
			LOG.error(message);
			throw new RuntimeException(message);
		});
	}

	@Override
	@Transactional
	public void setStatus(String key, Status status) {
		Connector connector = getByKey(key);
		Connector connectorUpdated = connectorStore.updateStatus(key, status);
		List<RegisteredEMSP> registeredEMSPs = registeredOperatorService.findEMSPs();
		LOG.info("Publishing asynchronously status [{}] for Connector with key [{}] on EMSPs with key {}...", status,
				key, registeredEMSPs.stream().map(RegisteredEMSP::getKey).toList());
		// We compute the evsePatchStatus outside of the task executor as there is no
		// session in the task executor
		// TODO Refactor
		com.duluthtechnologies.ocpi.model.v211.EVSE evsePatchStatus = connectorMapper
				.toEvsePatchStatus(connectorUpdated.getEvse());
		taskExecutor.execute(() -> {
			for (RegisteredEMSP registeredEMSP : registeredEMSPs) {
				LOG.debug("Publishing status [{}] of Connector with key [{}] on EMSP with key [{}]...", status, key,
						registeredEMSP.getKey());
				try {
					if (registeredEMSP instanceof RegisteredEMSPV211 registeredEMSPV211) {
						if (registeredEMSPV211.getOutgoingToken() != null
								&& registeredEMSPV211.getLocationsUrl() != null) {
							ocpiApiClient.patchEvseV211(registeredEMSPV211.getOutgoingToken(),
									registeredEMSPV211.getLocationsUrl(), cpoInfo.get().getCountryCode(),
									cpoInfo.get().getPartyId(), connector.getEvse().getLocation().getOcpiId(),
									connector.getEvse().getOcpiId(), evsePatchStatus);
						}
					}
				} catch (Exception e) {
					String message = "Exception caught while publishing status [%s] of Connector with key [%s] on EMSP with key [%s]"
							.formatted(status, key, registeredEMSP.getKey());
					LOG.error(message, e); // No rethrow as we want to continue
				}
			}
		});
	}

}
