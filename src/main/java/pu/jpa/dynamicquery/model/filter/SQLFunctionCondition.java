package pu.jpa.dynamicquery.model.filter;

import java.util.Map;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Setter;
import pu.jpa.dynamicquery.api.SQLFunctionType;

/**
 * @author Plamen Uzunov
 */
@Setter
public class SQLFunctionCondition extends AbstractCondition<Integer> {

    private SQLFunctionType function;

    @SuppressWarnings("unchecked")
    public Predicate toPredicate(@Nonnull CriteriaBuilder criteriaBuilder,
                                 @Nonnull Root<?> root,
                                 @Nullable Map<String, Join<Object, Object>> attributeToJoin) {
        Path<Integer> expression = getExpression(root, attributeToJoin);
        if (value == null) {
            value = createVal(strValue, (Class<Integer>) expression.getJavaType());
        }
        Expression<Integer> funct = criteriaBuilder.function(function.toString(), Integer.class, expression, criteriaBuilder.literal(value));
        return criteriaBuilder.greaterThan(funct, 0);
    }

}
