package pu.jpa.dynamicquery.api;

import java.util.List;
import java.util.Map;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * @author Plamen Uzunov
 */
public interface Expression {

    /**
     * Add the given expression to the expressions list.
     *
     * @param expression the expression to be added
     */
    void addExpression(Expression expression);

    /**
     * Returns collected filters of all the expressions.
     *
     * @return collected filters of all the expressions
     */
    List<Condition<?>> getConditions();

    /**
     * This method should be invoked by the JPA Specification implementation to build a {@link Predicate} used to create a WHERE clause
     * for a query of the referenced entity.
     *
     * @param root            must not be {@literal null}.
     * @param query           must not be {@literal null}.
     * @param criteriaBuilder must not be {@literal null}.
     * @return a {@link Predicate}, may be {@literal null}.
     */
     Predicate toPredicate(@Nonnull Root<?> root,
                           @Nonnull CriteriaQuery<?> query,
                           @Nonnull CriteriaBuilder criteriaBuilder,
                           @Nullable Map<String, Join<Object, Object>> attributeToJoin);

}
