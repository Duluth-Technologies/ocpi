package com.duluthtechnologies.ocpi.service.impl;

import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.service.RegisteredOperatorService;

import jakarta.annotation.PostConstruct;

@Component
public class LocationSynchronizer {

	private static final Logger LOG = LoggerFactory.getLogger(LocationServiceImpl.class);

	private final boolean emspSyncEnabled;

	private final Integer emspSyncIntervalInSeconds;

	private final RegisteredOperatorService registeredOperatorService;

	private final LocationServiceImpl locationServiceImpl;

	private final TaskScheduler taskScheduler;

	public LocationSynchronizer(@Qualifier("emspSyncIntervalInSeconds") Integer emspSyncIntervalInSeconds,
			@Qualifier("emspSyncEnabled") boolean emspSyncEnabled,
			@Qualifier("service-task-scheduler") TaskScheduler taskScheduler,
			RegisteredOperatorService registeredOperatorService, LocationServiceImpl locationServiceImpl) {
		super();
		this.emspSyncEnabled = emspSyncEnabled;
		this.emspSyncIntervalInSeconds = emspSyncIntervalInSeconds;
		this.registeredOperatorService = registeredOperatorService;
		this.locationServiceImpl = locationServiceImpl;
		this.taskScheduler = taskScheduler;
	}

	@PostConstruct
	public void initialize() {
		if (emspSyncEnabled) {
			LOG.info("Scheduling EMSP synchronization at interval of [{}] seconds...", emspSyncIntervalInSeconds);
			taskScheduler.scheduleAtFixedRate(() -> {
				try {
					synchronizeWithRegisteredCPOs();
				} catch (Exception e) {
					String message = "Exception caught while synchronizing with registered CPOs.";
					LOG.error(message, e); // No retrhow to keep the job scheduled
				}
			}, Duration.ofSeconds(emspSyncIntervalInSeconds));
		}
	}

	private void synchronizeWithRegisteredCPOs() {
		LOG.debug("Synchronizing with registered CPOs...");
		List<RegisteredCPO> registeredCPOs = registeredOperatorService.findCPOs();
		for (RegisteredCPO registeredCPO : registeredCPOs) {
			try {
				locationServiceImpl.synchronizeWithRegisteredCpo(registeredCPO.getKey());
			} catch (Exception e) {
				String message = "Exception caught while synchronizing with registered CPO with key [%s]"
						.formatted(registeredCPO.getKey());
				LOG.error(message, e);
			}
		}
	}
}
