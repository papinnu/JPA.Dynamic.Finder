package pu.jpa.dynamicquery.api;

import lombok.Getter;

/**
 * @author Plamen Uzunov
 */
public enum SQLFunctionType {

    BIT_AND((byte) 1, "bitwise_and", " & "),
    BIT_OR((byte) 2, "bitwise_or",  " | "),
    BIT_XOR((byte) 3, "bitwise_xor", " ^ "),
    ;

    @Getter
    private final byte code;
    private final String value;
    @Getter
    private final String operator;

    SQLFunctionType(byte code, String value, String operator) {
        this.code = code;
        this.value = value;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return value;
    }

    public static SQLFunctionType lookupCode(byte code) {
        for (SQLFunctionType b : SQLFunctionType.values()) {
            if (b.code == code) {
                return b;
            }
        }
        return null;
    }
}
