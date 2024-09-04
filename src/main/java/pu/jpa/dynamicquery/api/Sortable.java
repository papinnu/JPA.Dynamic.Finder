package pu.jpa.dynamicquery.api;

/**
 * @author Plamen Uzunov
 */
public interface Sortable {

    /**
     *  Sorting field name.
     */
    String getField();

    /**
     *  Sorting type: ASC | DESC.
     */
    SortType getType();
}
