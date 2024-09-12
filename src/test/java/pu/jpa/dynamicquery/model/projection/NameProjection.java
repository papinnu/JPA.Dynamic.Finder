package pu.jpa.dynamicquery.model.projection;

import pu.jpa.dynamicquery.api.Projection;

/**
 * @author Plamen Uzunov
 */

public record NameProjection(long id, String name) implements Projection {

}
