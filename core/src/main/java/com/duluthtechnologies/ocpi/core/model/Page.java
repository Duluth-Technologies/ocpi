package com.duluthtechnologies.ocpi.core.model;

import java.util.List;

public record Page<T>(List<T> content, int total) {

}
