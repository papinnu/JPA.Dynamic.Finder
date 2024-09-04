package pu.jpa.dynamicquery.model.filter;

import java.util.List;
import java.util.Map;

import jakarta.annotation.Nullable;
import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;
import pu.jpa.dynamicquery.api.Condition;
import pu.jpa.dynamicquery.api.Expression;

/**
 * The simple expression holds a simple condition implementation object instance used
 * to build the predicate to be inserted in the WHERE clause of the SELECT SQL query.
 * @author Plamen Uzunov
 */
@Getter
@Setter
public final class SimpleExpression extends AbstractExpression {

    private final Condition<?> filter;

    public SimpleExpression(@NonNull Condition<?> filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(@Nonnull Root<?> root,
                                 @Nonnull CriteriaQuery<?> query,
                                 @Nonnull CriteriaBuilder criteriaBuilder,
                                 @Nullable Map<String, Join<Object, Object>> attributeToJoin) {
        return filter.toPredicate(criteriaBuilder, root, attributeToJoin);
    }

    @Override
    public void addExpression(Expression expression) {
        throw new UnsupportedOperationException("Not supported! SimpleExpression doesn't have nested expressions.");
    }

    @Override
    public List<Expression> getExpressions() {
        throw new UnsupportedOperationException("Not supported! SimpleExpression doesn't have nested expressions.");
    }

    @Override
    public List<Condition<?>> getConditions() {
        return List.of(filter);
    }

}
