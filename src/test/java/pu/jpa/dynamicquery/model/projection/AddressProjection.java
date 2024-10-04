package pu.jpa.dynamicquery.model.projection;

import pu.jpa.dynamicquery.api.Projection;

/**
 * @author Plamen Uzunov
 */
public record AddressProjection(long addressId, String address, String city, String state, String zip, String country) implements Projection {

}
