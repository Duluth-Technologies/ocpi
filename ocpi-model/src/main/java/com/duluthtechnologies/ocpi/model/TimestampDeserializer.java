package com.duluthtechnologies.ocpi.model;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class TimestampDeserializer extends JsonDeserializer<Instant> {
	
	private static final Logger LOG = LoggerFactory.getLogger(TimestampDeserializer.class);

	private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("Z")),
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.of("Z")));

	@Override
	public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		String value = p.getValueAsString();
		for (DateTimeFormatter formatter : FORMATTERS) {
			try {
				return ZonedDateTime.parse(value, formatter).toInstant();
			} catch (DateTimeParseException e) {
				// do nothing, try the next formatter
			}
		}
		String message = "Unparseable timestamp [%s]".formatted(value);
		LOG.error(message);
		throw new JsonParseException(p, message);
	}

}
