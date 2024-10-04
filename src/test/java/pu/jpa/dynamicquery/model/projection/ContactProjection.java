package pu.jpa.dynamicquery.model.projection;

import pu.jpa.dynamicquery.api.Projection;

/**
 * @author Plamen Uzunov
 */
public record ContactProjection(long contactId, String contactName, String type,  String contactVal) implements Projection {
}
