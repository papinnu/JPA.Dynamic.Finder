package pu.jpa.dynamicquery.api;

import java.util.Map;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * @author Plamen Uzunov
 */
public interface Condition<V> {

    String getName();

    V getValue();

    void setEntity(Class<?> entity);
    void setMappedName(String mappedName);

    Predicate toPredicate(@Nonnull CriteriaBuilder criteriaBuilder,
                          @Nonnull Root<?> root,
                          @Nullable Map<String, Join<Object, Object>> attributeToJoin);

}
