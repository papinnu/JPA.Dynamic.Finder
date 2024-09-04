package pu.jpa.dynamicquery.api;

import java.util.List;

/**
 * @author Plamen Uzunov
 */
public interface Pageable {

    Expression getFilter();

    int getPage();

    int getPageSize();

    List<Sortable> getSort();

}
