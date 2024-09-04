package pu.jpa.dynamicquery.model.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pu.jpa.dynamicquery.api.SortType;
import pu.jpa.dynamicquery.api.Sortable;

/**
 * @author Plamen Uzunov
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sort implements Sortable {

    /* Sorting field name. */
    String field;

    /* Sorting type: ASC | DESC.*/
    SortType type;
}
