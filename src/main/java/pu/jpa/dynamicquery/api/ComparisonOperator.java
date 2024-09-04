package pu.jpa.dynamicquery.api;

import lombok.Getter;

/**
 * @author Plamen Uzunov
 */
@Getter
public enum ComparisonOperator {
    ANY((byte) 0, "any", false),
    EQUAL((byte) 1, "equal", false),
    NOT_EQUAL((byte) 2, "not_equal", false),
    CONTAINS((byte) 3, "contains", false),
    STARTS_WITH((byte) 4, "starts_with", false),
    ENDS_WITH((byte) 5, "ends_with", false),
    GT((byte) 6, "greater_than", false),
    GTE((byte) 7, "greater_than_or_equal", false),
    LT((byte) 8, "lower_than", false),
    LTE((byte) 9, "lower_than_or_equal", false),
    BETWEEN((byte) 10, "between", false),
    IN((byte) 11, "in", true),
    IS_NULL((byte) 12, "is_null", false),
    NOT_NULL((byte) 13, "not_null", false),

//    EMPTY((byte) 11, "empty", false),
//    NOT_EMPTY((byte) 12, "not_empty", false),
//    BEFORE((byte) 13, "before", false),
//    BEFOREE((byte) 13, "before_or_equal", false),
//    AFTER((byte) 14, "after", false),
//    AFTERE((byte) 15, "after_or_equal", false),
    ;

    @Getter
    private final byte code;
    private final String value;

    @Getter
    private final boolean compound;

    ComparisonOperator(byte code, String value, boolean compound) {
        this.code = code;
        this.value = value;
        this.compound = compound;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ComparisonOperator lookupCode(byte code) {
        for (ComparisonOperator b : ComparisonOperator.values()) {
            if (b.code == code) {
                return b;
            }
        }
        return ANY; // default comparison method
    }

}
