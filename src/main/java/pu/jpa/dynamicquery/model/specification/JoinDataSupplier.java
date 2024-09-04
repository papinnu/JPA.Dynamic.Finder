package pu.jpa.dynamicquery.model.specification;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import pu.jpa.dynamicquery.model.projection.Projection;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

/**
 * @author Plamen Uzunov
 */
public interface JoinDataSupplier<E,V extends Projection> {

    default Map<String, Join<Object, Object>> getJoinData(Root<E> root, CriteriaQuery<V> query, CriteriaBuilder builder) {
        return new LinkedHashMap<>();
    }

}
