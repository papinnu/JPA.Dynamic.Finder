package pu.jpa.dynamicquery.model.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Setter;
import pu.jpa.dynamicquery.api.ComparisonOperator;

import static pu.jpa.dynamicquery.util.StringUtil.objectFromString;

/**
 * @author Plamen Uzunov
 */
@Setter
public class CompositeFilter<C extends Comparable<C>> extends AbstractCondition<C> {

    private Collection<C> val;

    public CompositeFilter(ComparisonOperator operator) {
        if (!operator.isCompound()) {
            throw new IllegalArgumentException("Comparison operator is not a compound type!");
        }
        setOperator(operator);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Predicate toPredicate(@Nonnull CriteriaBuilder criteriaBuilder,
                                 @Nonnull Root<?> root,
                                 @Nullable Map<String, Join<Object, Object>> attributeToJoin) {
        Path<C> expression = getExpression(root, attributeToJoin);
        CriteriaBuilder.In<C> exp = criteriaBuilder.in(expression);
        getAndCreateIfAbsent((Class<C>) exp.getJavaType()).forEach(exp::value);
        return exp;
    }

    private Collection<C> getAndCreateIfAbsent(Class<C> clazz) {
        if (val == null && strValue != null && strValue.isBlank()) {
            val = Arrays.stream(strValue.split(","))
                .map(String::trim)
                .map(strVal -> objectFromString(clazz, strVal))
                .collect(Collectors.toList());
        }
        return val;
    }

}
