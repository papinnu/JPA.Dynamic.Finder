package pu.jpa.dynamicquery.model.filter;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pu.jpa.dynamicquery.api.Expression;

/**
 * @author Plamen Uzunov
 */
@Getter
@Setter
public abstract class AbstractExpression implements Expression {

    /**
     * Optional nested expressions list.
     */
    private final List<Expression> expressions;


    public AbstractExpression() {
        this(new ArrayList<>());
    }

    public AbstractExpression(List<Expression> expressions) {
        this.expressions = expressions;
    }

    /**
     * Add the given expression to the expressions list.
     * @param expression the expression to add
     */
    @Override
    public void addExpression(Expression expression) {
        expressions.add(expression);
    }

}
