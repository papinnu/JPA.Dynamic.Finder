package pu.jpa.dynamicquery.model.filter;

import java.util.Map;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.Setter;
import pu.jpa.dynamicquery.api.ComparisonOperator;
import pu.jpa.dynamicquery.api.Condition;

/**
 * @author Plamen Uzunov
 */
@Setter
public abstract class AbstractCondition<V> implements Condition<V> {

    @Getter
    private String name;

    protected String strValue;

    @Getter
    protected V value;

    protected ComparisonOperator operator;

    @Getter
    private Class<?> entity;

    @Getter
    private String mappedName;

    protected Path<V> getExpression(@Nonnull Root<?> root, @Nullable Map<String, Join<Object, Object>> attributeToJoin) {
        Path<V> expression = null;
        if (attributeToJoin != null && !attributeToJoin.isEmpty()) {
            String[] paths = getName().split("\\.");
            if (paths.length > 1) {
                Join<?, ?> join = null;
                for (String path : paths) {
                    Join<?, ?> currentJoin = attributeToJoin.get(path);
                    if (currentJoin != null) {
                        join = currentJoin;
                    } else {
                        break;
                    }
                }
                if (join != null) {
                    String name = paths[paths.length - 1];
                    expression = join.get(name);
                }
            }
        }
        if (expression == null) {
            expression = root.get(getName());
        }
        return expression;
    }

}
