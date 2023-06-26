package com.duluthtechnologies.ocpi.api.ocpi.security;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.duluthtechnologies.ocpi.core.context.SecurityContext;
import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;
import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;
import com.duluthtechnologies.ocpi.core.model.RegisteredOperator;
import com.duluthtechnologies.ocpi.core.service.RegisteredOperatorService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OcpiTokenFilter extends OncePerRequestFilter {

	private static final Logger LOG = LoggerFactory.getLogger(OcpiTokenFilter.class);

	private final RegisteredOperatorService registeredOperatorService;

	public OcpiTokenFilter(RegisteredOperatorService registeredOperatorService) {
		this.registeredOperatorService = registeredOperatorService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		if (!path.startsWith("/ocpi/")) {
			chain.doFilter(request, response);
			return;
		}
		// Get authorization header
		final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		// If authorization header isn't here, just continue the chain of filters
		if (!StringUtils.hasText(header) || !header.startsWith("Token ")) {
			LOG.debug("No OCPI token has been provided for request to path [{}].", path);
			chain.doFilter(request, response);
			return;
		}
		// Get OCPI token
		final String token = header.split(" ")[1].trim();
		LOG.debug("OCPI token has been provided for request to path [{}].", path);
		try {
			Optional<RegisteredOperator> registeredOperator = registeredOperatorService.findByIncomingToken(token);
			if (registeredOperator.isPresent()) {
				if (registeredOperator.get() instanceof RegisteredCPO) {
					String key = registeredOperator.get().getKey();
					LOG.debug("OCPI token matches CPO with key [{}].", key);
					SecurityContext.setCPOKey(key);
				} else if (registeredOperator.get() instanceof RegisteredEMSP) {
					String key = registeredOperator.get().getKey();
					LOG.debug("OCPI token matches EMSP with key [{}].", key);
					SecurityContext.setEMSPKey(key);
				}
			} else {
				// TODO Return 401 with specific OCPI error code
				String message = "No registered operator matching the incoming token";
				LOG.error(message);
				throw new RuntimeException(message);
			}
			chain.doFilter(request, response);
		} finally {
			// SecurityContext must be cleared no matter what
			SecurityContext.clear();
		}
	}
}
