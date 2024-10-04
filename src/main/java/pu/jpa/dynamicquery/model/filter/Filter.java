package pu.jpa.dynamicquery.model.filter;

import java.util.Map;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple condition implementation to build a Predicate JPA API instance from the defined logical operation
 * and the required values for it.
 *
 * @author Plamen Uzunov
 */
//@Builder
@Getter
@Setter
@NoArgsConstructor
public class Filter<C extends Comparable<C>> extends AbstractCondition<C> {

    private static final Logger LOG = LoggerFactory.getLogger(Filter.class);

    private String strSecondValue;

    @Setter
    protected C secondValue;

    @SuppressWarnings("unchecked")
    @Override
    public Predicate toPredicate(@Nonnull CriteriaBuilder criteriaBuilder,
                                 @Nonnull Root<?> root,
                                 @Nullable Map<String, Join<Object, Object>> attributeToJoin) {
        Path<C> expression = getExpression(root, attributeToJoin);
        if (value == null) {
            value = createVal(strValue, (Class<C>) expression.getJavaType());
        }
        if (secondValue == null) {
            secondValue = createVal(strSecondValue, (Class<C>) expression.getJavaType());
        }
        return switch (operator) {
            case EQUAL -> buildEqualPredicate(criteriaBuilder, (Path<String>) expression);
            case CONTAINS -> buildContainsPredicate(criteriaBuilder, (Path<String>) expression);
            case STARTS_WITH -> buildStartsWithPredicate(criteriaBuilder, (Path<String>) expression);
            case ENDS_WITH -> buildEndsWithPredicate(criteriaBuilder, (Path<String>) expression);
            case BETWEEN -> criteriaBuilder.between(expression, value, secondValue);
            case GT -> criteriaBuilder.greaterThan(expression, value);
            case LT -> criteriaBuilder.lessThan(expression, value);
            case GTE -> criteriaBuilder.greaterThanOrEqualTo(expression, value);
            case LTE -> criteriaBuilder.lessThanOrEqualTo(expression, value);
            case NOT_EQUAL -> buildNotEqualPredicate(criteriaBuilder, (Path<String>) expression);
            case IS_NULL -> criteriaBuilder.isNull(expression);
            case NOT_NULL -> criteriaBuilder.isNotNull(expression);
            default -> {
                LOG.error("Invalid comparison method:{}", operator);
                throw new IllegalArgumentException(operator + " is not valid comparison method");
            }
        };

    }

    private Predicate buildEqualPredicate(CriteriaBuilder criteriaBuilder, Path<String> expression) {
        return (String.class.equals(expression.getJavaType()))
            ? criteriaBuilder.equal(criteriaBuilder.lower(expression), criteriaBuilder.lower(criteriaBuilder.literal(strValue)))
            : criteriaBuilder.equal(expression, value);
    }

    private Predicate buildNotEqualPredicate(CriteriaBuilder criteriaBuilder, Path<String> expression) {
        return (String.class.equals(expression.getJavaType()))
            ? criteriaBuilder.notEqual(criteriaBuilder.lower(expression), criteriaBuilder.lower(criteriaBuilder.literal(strValue)))
            : criteriaBuilder.notEqual(expression, value);
    }

    private Predicate buildStartsWithPredicate(CriteriaBuilder criteriaBuilder, Path<String> expression) {
        if (String.class.equals(expression.getJavaType())) {
            return criteriaBuilder.like(criteriaBuilder.lower(expression), criteriaBuilder.lower(criteriaBuilder.literal(value + "%")));
        }
        throw new IllegalArgumentException("The 'Starts with' operator supports only consecutive character values. [Java type:" + expression.getJavaType() + " | value:" + value);
    }

    private Predicate buildContainsPredicate(CriteriaBuilder criteriaBuilder, Path<String> expression) {
        if (String.class.equals(expression.getJavaType())) {
            return criteriaBuilder.like(criteriaBuilder.lower(expression), criteriaBuilder.lower(criteriaBuilder.literal("%" + value + "%")));
        }
        throw new IllegalArgumentException("The 'Contains' operator supports only consecutive character values. [Java type:" + expression.getJavaType() + " | value:" + value);
    }

    private Predicate buildEndsWithPredicate(CriteriaBuilder criteriaBuilder, Path<String> expression) {
        if (String.class.equals(expression.getJavaType())) {
            return criteriaBuilder.like(criteriaBuilder.lower(expression), criteriaBuilder.lower(criteriaBuilder.literal("%" + value)));
        }
        throw new IllegalArgumentException("The 'Ends with' operator supports only consecutive character values. [Java type:" + expression.getJavaType() + " | value:" + value);
    }

}
