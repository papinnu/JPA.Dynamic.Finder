package pu.jpa.dynamicquery.model.filter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import pu.jpa.dynamicquery.api.Condition;
import pu.jpa.dynamicquery.api.Expression;
import pu.jpa.dynamicquery.api.LogicalOperator;

/**
 * @author Plamen Uzunov
 */
public class CompositeExpression extends AbstractExpression {

    private final LogicalOperator logicalOperator;

    public CompositeExpression(LogicalOperator logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public CompositeExpression(LogicalOperator logicalOperator, List<Expression> expressions) {
        super(expressions);
        this.logicalOperator = logicalOperator;
    }

    @Override
    public Predicate toPredicate(@Nonnull Root<?> root,
                                 @Nonnull CriteriaQuery<?> query,
                                 @Nonnull CriteriaBuilder criteriaBuilder,
                                 @Nullable Map<String, Join<Object, Object>> attributeToJoin) {
        return switch (logicalOperator) {
            case AND -> criteriaBuilder.and(getExpressions().stream()
                .map(queryExpression -> queryExpression.toPredicate(root, query, criteriaBuilder, attributeToJoin))
                .toList().toArray(Predicate[]::new));
            case OR -> criteriaBuilder.or(getExpressions().stream()
                .map(queryExpression -> queryExpression.toPredicate(root, query, criteriaBuilder, attributeToJoin))
                .toList().toArray(Predicate[]::new));
        };
    }

    @Override
    public List<Condition<?>> getConditions() {
        return getExpressions().stream()
            .flatMap(queryExpression -> queryExpression.getConditions().stream())
            .collect(Collectors.toList());
    }

}
