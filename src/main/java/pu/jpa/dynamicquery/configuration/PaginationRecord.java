package pu.jpa.dynamicquery.configuration;

import javax.validation.Valid;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Plamen Uzunov
 */
@Valid
@ConfigurationProperties(prefix = "application.jpa.dynamic-query.pagination")
public record PaginationRecord(int pageSize) {
}
