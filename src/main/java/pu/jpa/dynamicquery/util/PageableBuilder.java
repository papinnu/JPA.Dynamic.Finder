package pu.jpa.dynamicquery.util;

import java.util.List;

import pu.jpa.dynamicquery.api.Expression;
import pu.jpa.dynamicquery.api.Pageable;
import pu.jpa.dynamicquery.api.Sortable;
import pu.jpa.dynamicquery.configuration.PaginationRecord;
import pu.jpa.dynamicquery.model.filter.Pagination;

/**
 * @author Plamen Uzunov
 */
public class PageableBuilder {

    private final PaginationRecord paginationRecord;

    public PageableBuilder(PaginationRecord paginationRecord) {
        this.paginationRecord = paginationRecord;
    }

    private Expression filter;
    private int page;
    private int pageSize;
    private List<Sortable> sort;

    public PageableBuilder withPageNumber(int pageNumber) {
        page = pageNumber;
        return this;
    }

    public PageableBuilder withPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public PageableBuilder withSort(List<Sortable> sort) {
        this.sort = sort;
        return this;
    }

    public PageableBuilder withFilter(Expression filter) {
        this.filter = filter;
        return this;
    }

    public Pageable build() {
        Pagination result = new Pagination();
        if(pageSize <= 0) {
            pageSize = paginationRecord.pageSize();
        }
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setSort(sort);
        result.setFilter(filter);
        return result;
    }

}
