package com.duluthtechnologies.ocpi.api.ocpi.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import com.duluthtechnologies.ocpi.core.configuration.EMSPInfo;

@Validated
@RestController
@ConditionalOnBean(EMSPInfo.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface EMSPController {

}
