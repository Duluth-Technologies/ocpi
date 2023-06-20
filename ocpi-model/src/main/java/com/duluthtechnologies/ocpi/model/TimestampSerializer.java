package com.duluthtechnologies.ocpi.model;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class TimestampSerializer  extends JsonSerializer<Instant> {
	
    // Set this to true if you want to include 'Z' in the serialized output
    public static boolean shouldSerializeWithZ = true;

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(shouldSerializeWithZ ?
                "yyyy-MM-dd'T'HH:mm:ss'Z'" : "yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.of("Z"));
        gen.writeString(formatter.format(value));
    }
}