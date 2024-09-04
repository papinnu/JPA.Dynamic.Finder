package pu.jpa.dynamicquery.model.filter;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import pu.jpa.dynamicquery.api.ComparisonOperator;
import pu.jpa.dynamicquery.api.Condition;

/**
 * @author Plamen Uzunov
 */
public class ConditionObjectFactory {

    private static final ConditionObjectFactory INSTANCE = new ConditionObjectFactory();

    public static ConditionObjectFactory getInstance() {
        return INSTANCE;
    }

    public Condition<?> createCondition(@Nonnull ComparisonOperator operator,
                                        @Nonnull String name,
                                        @Nonnull String value,
                                        @Nullable String secondValue) {
        return (operator.isCompound())
            ? createCompoundCondition(operator, name, value)
            : createCondition(name, value, secondValue, operator);
    }

    public Condition<?> createCondition(int operatorCode,
                                        @Nonnull String name,
                                        @Nonnull String value,
                                        @Nullable String secondValue) {
        ComparisonOperator operator = ComparisonOperator.lookupCode((byte) operatorCode);
        return createCondition(operator, name, value, secondValue);
    }

    public Condition<?> createCondition(@Nonnull JsonNode node) {
        JsonNode operatorNode = node.get("method");
        String name = getNodeValue(node, "name");
        String value = getNodeValue(node, "val");
        return (operatorNode != null & name != null && value != null)
            ? createCondition(operatorNode.intValue(),
            name,
            value,
            getNodeValue(node, "val1"))
            : null;
    }

    private Condition<?> createCondition(String name, String value, String secondValue, ComparisonOperator operator) {
        Filter<?> filter = new Filter<>();
        filter.setName(name);
        filter.setStrValue(value);
        filter.setStrSecondValue(secondValue);
        filter.setOperator(operator);
        return filter;
    }

    private Condition<?> createCompoundCondition(ComparisonOperator operator, String name, String value) {
        CompositeFilter<Integer> compoundFilter = new CompositeFilter<>(operator);
        compoundFilter.setName(name);
        compoundFilter.setStrValue(value);
        return compoundFilter;
    }

    private String getNodeValue(@Nonnull JsonNode node, String name) {
        if (name.isBlank()) {
            return null;
        }
        JsonNode child = node.get(name);
        return (child != null) ? child.textValue() : null;
    }

    private ConditionObjectFactory() {
    }

}
