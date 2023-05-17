package com.duluthtechnologies.ocpi.persistence.entity;

import com.duluthtechnologies.ocpi.core.model.RegisteredEMSP;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "registered_emsps")
public class RegisteredEMSPEntity extends RegisteredOperatorEntity implements RegisteredEMSP {

}
