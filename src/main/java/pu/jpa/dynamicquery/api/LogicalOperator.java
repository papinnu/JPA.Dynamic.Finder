package pu.jpa.dynamicquery.api;

/**
 * @author Plamen Uzunov
 */
public enum LogicalOperator {
    AND(" and "),
    OR(" or "),
    ;

    private final String operator;

    LogicalOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return operator;
    }

}
