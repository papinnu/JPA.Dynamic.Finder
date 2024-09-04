package pu.jpa.dynamicquery.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import pu.jpa.dynamicquery.api.Condition;
import pu.jpa.dynamicquery.api.LogicalOperator;
import pu.jpa.dynamicquery.api.SortType;
import pu.jpa.dynamicquery.api.Sortable;
import pu.jpa.dynamicquery.model.filter.AbstractExpression;
import pu.jpa.dynamicquery.model.filter.CompositeExpression;
import pu.jpa.dynamicquery.model.filter.ConditionObjectFactory;
import pu.jpa.dynamicquery.model.filter.Pagination;
import pu.jpa.dynamicquery.model.filter.SimpleExpression;
import pu.jpa.dynamicquery.model.filter.Sort;

/**
 * @author Plamen Uzunov
 */
public class ExpressionDeserializer extends JsonDeserializer<Pagination> {

    @Override
    public Pagination deserialize (JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode root = parser.getCodec().readTree(parser);
        Pagination result = new Pagination();
        if (root.has("number")) {
            result.setPage(root.get("number").intValue());
        }
        if (root.has("size")) {
            result.setPageSize(root.get("size").intValue());
        }
        if (root.has("expression")) {
            JsonNode expressionNode = root.get("expression");
            result.setFilter(handleExpression(expressionNode));
        }
        if (root.has("sort")) {
            JsonNode sortNode = root.get("sort");
            result.setSort(handleSort(sortNode));
        }
        return result;
    }

    private AbstractExpression handleExpression (JsonNode expressionNode){
        if (expressionNode.has("and")) {
            CompositeExpression expression = new CompositeExpression(LogicalOperator.AND);
            handleExpression(expression, expressionNode.get("and"));
            return expression;
        } else if (expressionNode.has("or")) {
            CompositeExpression expression = new CompositeExpression(LogicalOperator.OR);
            handleExpression(expression, expressionNode.get("or"));
            return expression;
        } else {
            Condition<?> condition = handleCondition(expressionNode);
            return new SimpleExpression(condition);
        }
    }

    private void handleExpression (AbstractExpression parent, JsonNode expressionNode){
        if (expressionNode.isArray()) {
            expressionNode.forEach(entry -> parent.addExpression(handleExpression(entry)));
        } else {
            Condition<?> condition = handleCondition(expressionNode);
            parent.addExpression(new SimpleExpression(condition));
        }
    }

    private Condition<?> handleCondition (JsonNode filterNode){
        return ConditionObjectFactory.getInstance().createCondition(filterNode);
    }

    private List<Sortable> handleSort (JsonNode sortNode){
        List<Sortable> sorts = new ArrayList<>();
        if (sortNode.isArray()) {
            sortNode.forEach(entry -> sorts.add(new Sort(entry.get("field").textValue(),
                SortType.lookup(entry.get("type").textValue()))));
        }
        return sorts;
    }
}
