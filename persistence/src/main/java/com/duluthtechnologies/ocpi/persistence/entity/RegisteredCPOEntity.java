package com.duluthtechnologies.ocpi.persistence.entity;

import com.duluthtechnologies.ocpi.core.model.RegisteredCPO;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "registered_cpos")
public class RegisteredCPOEntity extends RegisteredOperatorEntity implements RegisteredCPO {

}
