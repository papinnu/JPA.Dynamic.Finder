package pu.jpa.dynamicquery.model.filter;

import java.util.List;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import pu.jpa.dynamicquery.api.Expression;
import pu.jpa.dynamicquery.api.LogicalOperator;
import pu.jpa.dynamicquery.api.Pageable;
import pu.jpa.dynamicquery.api.Sortable;
import pu.jpa.dynamicquery.util.ExpressionDeserializer;

/**
 * Represents the pagination request with page number, page size, sort and filter criteria.
 * The returned result optionally can be sorted by the list of sorting fields with their sorting direction.
 * @author Plamen Uzunov
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = ExpressionDeserializer.class)
@JsonTypeName("pagination")
@JsonTypeInfo(include= JsonTypeInfo.As.WRAPPER_OBJECT,use= JsonTypeInfo.Id.NAME)
public class Pagination implements Pageable {

    @JsonProperty("expression")
    private Expression filter;

    @Min(0)
    @JsonProperty("number")
    private int page = 0; // first page - starting from 0 (zero)

    @Min(1)
    @JsonProperty("size")
    private int pageSize;

    @JsonProperty("sort")
    private List<Sortable> sort;

    public Expression getFilterAndCreateIfAbsent(LogicalOperator operator) {
        if (filter == null) {
            filter = new CompositeExpression(operator);
        }
        return filter;
    }

}
