package com.duluthtechnologies.ocpi.persistence.impl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.duluthtechnologies.ocpi.core.model.ChargingSession;
import com.duluthtechnologies.ocpi.core.model.Page;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSPChargingSession;
import com.duluthtechnologies.ocpi.core.store.ChargingSessionStore;
import com.duluthtechnologies.ocpi.persistence.entity.ChargingSessionEntity;
import com.duluthtechnologies.ocpi.persistence.entity.ConnectorEntity;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredCPOEntity;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredEMSPChargingSessionEntity;
import com.duluthtechnologies.ocpi.persistence.entity.RegisteredEMSPEntity;
import com.duluthtechnologies.ocpi.persistence.jpa.ChargingSessionJPARepository;
import com.duluthtechnologies.ocpi.persistence.jpa.ConnectorJPARepository;
import com.duluthtechnologies.ocpi.persistence.jpa.RegisteredOperatorJPARepository;
import com.duluthtechnologies.ocpi.persistence.mapper.ChargingSessionEntityMapper;

import jakarta.transaction.Transactional;

@Component
public class ChargingSessionStoreImpl implements ChargingSessionStore {

	private static final Logger LOG = LoggerFactory.getLogger(ChargingSessionStoreImpl.class);

	private final ChargingSessionJPARepository chargingSessionJPARepository;

	private final ConnectorJPARepository connectorJPARepository;

	private final RegisteredOperatorJPARepository registeredOperatorJPARepository;

	private final ChargingSessionEntityMapper chargingSessionEntityMapper;

	public ChargingSessionStoreImpl(ChargingSessionJPARepository chargingSessionJPARepository,
			ChargingSessionEntityMapper chargingSessionEntityMapper,
			RegisteredOperatorJPARepository registeredOperatorJPARepository,
			ConnectorJPARepository connectorJPARepository) {
		super();
		this.chargingSessionJPARepository = chargingSessionJPARepository;
		this.connectorJPARepository = connectorJPARepository;
		this.registeredOperatorJPARepository = registeredOperatorJPARepository;
		this.chargingSessionEntityMapper = chargingSessionEntityMapper;
	}

	@Override
	public Page<RegisteredEMSPChargingSession> findRegisteredEMSPChargingSessions(String registeredEmspKey,
			Instant dateFrom, Instant dateTo, Integer offset, Integer limit) {
		Pageable pageable = new OffsetBasedPageRequest(offset, limit, Sort.by("lastModifiedDate").descending());
		List<RegisteredEMSPChargingSession> content = chargingSessionJPARepository
				.findByRegisteredEMSPKeyAndLastModifiedDateBetween(registeredEmspKey, dateFrom, dateTo, pageable)
				.stream().map(RegisteredEMSPChargingSession.class::cast).toList();
		return new Page(content, content.size());
	}

	public class OffsetBasedPageRequest implements Pageable, Serializable {

		private static final long serialVersionUID = -25822475129613575L;

		private int limit;
		private long offset;
		private final Sort sort;

		/**
		 * Creates a new {@link OffsetBasedPageRequest} with sort parameters applied.
		 *
		 * @param offset zero-based offset.
		 * @param limit  the size of the elements to be returned.
		 * @param sort   can be {@literal null}.
		 */
		public OffsetBasedPageRequest(long offset, int limit, Sort sort) {
			if (offset < 0) {
				throw new IllegalArgumentException("Offset index must not be less than zero!");
			}

			if (limit < 1) {
				throw new IllegalArgumentException("Limit must not be less than one!");
			}
			this.limit = limit;
			this.offset = offset;
			this.sort = sort;
		}

		/**
		 * Creates a new {@link OffsetBasedPageRequest} with sort parameters applied.
		 *
		 * @param offset     zero-based offset.
		 * @param limit      the size of the elements to be returned.
		 * @param direction  the direction of the {@link Sort} to be specified, can be
		 *                   {@literal null}.
		 * @param properties the properties to sort by, must not be {@literal null} or
		 *                   empty.
		 */
		public OffsetBasedPageRequest(long offset, int limit, Sort.Direction direction, String... properties) {
			this(offset, limit, Sort.by(direction, properties));
		}

		/**
		 * Creates a new {@link OffsetBasedPageRequest} with sort parameters applied.
		 *
		 * @param offset zero-based offset.
		 * @param limit  the size of the elements to be returned.
		 */
		public OffsetBasedPageRequest(long offset, int limit) {
			this(offset, limit, Sort.unsorted());
		}

		@Override
		public int getPageNumber() {
			return (int) (offset / limit);
		}

		@Override
		public int getPageSize() {
			return limit;
		}

		@Override
		public long getOffset() {
			return offset;
		}

		@Override
		public Sort getSort() {
			return sort;
		}

		@Override
		public Pageable next() {
			return new OffsetBasedPageRequest(getOffset() + getPageSize(), getPageSize(), getSort());
		}

		public OffsetBasedPageRequest previous() {
			return hasPrevious() ? new OffsetBasedPageRequest(getOffset() - getPageSize(), getPageSize(), getSort())
					: this;
		}

		@Override
		public Pageable previousOrFirst() {
			return hasPrevious() ? previous() : first();
		}

		@Override
		public Pageable first() {
			return new OffsetBasedPageRequest(0, getPageSize(), getSort());
		}

		@Override
		public boolean hasPrevious() {
			return offset > limit;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;

			if (!(o instanceof OffsetBasedPageRequest))
				return false;

			OffsetBasedPageRequest that = (OffsetBasedPageRequest) o;

			return new EqualsBuilder().append(limit, that.limit).append(offset, that.offset).append(sort, that.sort)
					.isEquals();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 37).append(limit).append(offset).append(sort).toHashCode();
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this).append("limit", limit).append("offset", offset).append("sort", sort)
					.toString();
		}

		@Override
		public Pageable withPage(int pageNumber) {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	@Transactional
	public RegisteredEMSPChargingSession create(RegisteredEMSPChargingSession registeredEMSPChargingSession) {
		ConnectorEntity connectorEntity = connectorJPARepository
				.findByKey(registeredEMSPChargingSession.getConnector().getKey()).orElseThrow(() -> {
					String message = "Cannot create Registered EMSP Charging Session as connector with key [%s] doesn't exist."
							.formatted(registeredEMSPChargingSession.getConnector().getKey());
					LOG.error(message);
					return new RuntimeException(message);
				});
		RegisteredEMSPEntity registeredEMSPEntity = (RegisteredEMSPEntity) registeredOperatorJPARepository
				.findByKey(registeredEMSPChargingSession.getRegisteredEMSP().getKey()).orElseThrow(() -> {
					String message = "Cannot create Registered EMSP Charging Session as Registered EMSP with key [%s] doesn't exist."
							.formatted(registeredEMSPChargingSession.getRegisteredEMSP().getKey());
					LOG.error(message);
					return new RuntimeException(message);
				});
		RegisteredEMSPChargingSessionEntity registeredEMSPChargingSessionEntity = chargingSessionEntityMapper
				.toRegisteredEMSPChargingSessionEntity(registeredEMSPChargingSession, connectorEntity,
						registeredEMSPEntity);
		return chargingSessionJPARepository.save(registeredEMSPChargingSessionEntity);

	}

	@Override
	public List<ChargingSession> findChargingSessions(Instant dateFrom, Instant dateTo, Optional<String> connectorKey) {
		if (connectorKey.isPresent()) {
			return chargingSessionJPARepository
					.findByConnectorKeyAndLastModifiedDateBetween(connectorKey.get(), dateFrom, dateTo).stream()
					.map(ChargingSession.class::cast).toList();
		} else {
			return chargingSessionJPARepository.findByLastModifiedDateBetween(dateFrom, dateTo).stream()
					.map(ChargingSession.class::cast).toList();
		}
	}

	@Override
	public Optional<ChargingSession> findChargingSessions(String countryCode, String partyId, String ocpiId) {
		// Look for the Registered CPO with the countryCode and partyId
		RegisteredCPOEntity registeredCPOEntity = (RegisteredCPOEntity) registeredOperatorJPARepository
				.findByCountryCodeAndPartyId(countryCode, partyId).orElseThrow(() -> {
					String message = "Cannot find Charging Sessions for country code [%s] and party id [%s] as no such Registered Operator exists."
							.formatted(countryCode, partyId);
					LOG.error(message);
					throw new RuntimeException(message);
				});
		// Use the Registered CPO key and the OCPI id to get the Session
		return chargingSessionJPARepository.findByRegisteredCPOKeyAndOcpiId(registeredCPOEntity.getKey(), ocpiId)
				.map(ChargingSession.class::cast);
	}

	@Override
	@Transactional
	public ChargingSession create(ChargingSession chargingSession) {
		ConnectorEntity connectorEntity = connectorJPARepository.findByKey(chargingSession.getConnector().getKey())
				.orElseThrow(() -> {
					String message = "Cannot create Charging Session as connector with key [%s] doesn't exist."
							.formatted(chargingSession.getConnector().getKey());
					LOG.error(message);
					return new RuntimeException(message);
				});
		ChargingSessionEntity chargingSessionEntity = chargingSessionEntityMapper
				.toChargingSessionEntity(chargingSession, connectorEntity);
		return chargingSessionJPARepository.save(chargingSessionEntity);
	}

	@Override
	public Optional<ChargingSession> findByKey(String chargingSessionKey) {
		return chargingSessionJPARepository.findByKey(chargingSessionKey).map(ChargingSession.class::cast);
	}

	@Override
	@Transactional
	public ChargingSession update(ChargingSession chargingSession) {
		ChargingSessionEntity chargingSessionEntity = chargingSessionJPARepository.findByKey(chargingSession.getKey())
				.orElseThrow(() -> {
					String message = "Cannot update ChargingSession with key [%s] as it cannot be found."
							.formatted(chargingSession.getKey());
					LOG.error(message);
					return new RuntimeException(message);
				});
		chargingSessionEntityMapper.updateChargingSessionEntity(chargingSessionEntity, chargingSession);
		return chargingSessionJPARepository.save(chargingSessionEntity);
	}

	@Override
	@Transactional
	public ChargingSession patch(ChargingSession chargingSession) {
		ChargingSessionEntity chargingSessionEntity = chargingSessionJPARepository.findByKey(chargingSession.getKey())
				.orElseThrow(() -> {
					String message = "Cannot update ChargingSession with key [%s] as it cannot be found."
							.formatted(chargingSession.getKey());
					LOG.error(message);
					return new RuntimeException(message);
				});
		chargingSessionEntityMapper.patchChargingSessionEntity(chargingSessionEntity, chargingSession);
		return chargingSessionJPARepository.save(chargingSessionEntity);
	}

}
