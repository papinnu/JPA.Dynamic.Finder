package pu.jpa.dynamicquery.util;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pu.jpa.dynamicquery.api.Sortable;
import pu.jpa.dynamicquery.configuration.PaginationRecord;
import pu.jpa.dynamicquery.model.filter.Pagination;
import pu.jpa.dynamicquery.model.projection.Projection;

/**
 * @author Plamen Uzunov
 */
@Component
public class JpaQueryBuilder {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    @Getter
    private PaginationRecord pagingRecord;

    public <E, R extends Projection> Page<R> getPage(@Nonnull TypedQuery<R> query,
                                                     @Nonnull Class<E> domainClass,
                                                     @Nonnull Pageable pageable,
                                                     @Nonnull Specification<E> spec) {
        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }
        return PageableExecutionUtils.getPage(query.getResultList(), pageable, () -> executeCountQuery(getCountQuery(spec, domainClass)));
    }

    public long executeCountQuery(@Nonnull TypedQuery<Long> countQuery) {
        List<Long> totals = countQuery.getResultList();
        long total = 0L;
        for (Long element : totals) {
            total += element == null ? 0 : element;
        }
        return total;
    }

    private PageRequest getPageRequest(Pagination paging) {
        if (paging.getPageSize() <= 0) {
            paging.setPageSize(pagingRecord.pageSize());
        }
        List<Sortable> sortOrderMetadataList = paging.getSort();
        List<org.springframework.data.domain.Sort.Order> orders = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sortOrderMetadataList)) {
            orders.addAll(sortOrderMetadataList.stream()
                .map(sortOrderMetadata -> switch (sortOrderMetadata.getType()) {
                    case ASC -> org.springframework.data.domain.Sort.Order.asc(sortOrderMetadata.getField());
                    case DESC ->  org.springframework.data.domain.Sort.Order.desc(sortOrderMetadata.getField());
                })
                .toList());
        }
        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(orders);
        return PageRequest.of(paging.getPage(), paging.getPageSize(), sort);
    }

    public <E, R extends Projection> Page<R> getPagedData(@Nonnull Class<E> domainClass,
                                                          @Nonnull Class<R> resultClass,
                                                          @Nonnull Specification<E> specification,
                                                          @Nonnull Pagination paging) {
        PageRequest request = getPageRequest(paging);
        TypedQuery<R> typedQuery = getTypedQuery(specification, domainClass, resultClass, request.getSort());
        return request.isUnpaged() ? new PageImpl<>(typedQuery.getResultList()) : getPage(typedQuery, domainClass, request, specification);
    }

    public <E, R extends Projection> TypedQuery<R> getTypedQuery(@Nonnull Specification<E> spec,
                                                                 @Nonnull Class<E> domainClass,
                                                                 @Nonnull Class<R> resultClass,
                                                                 @Nonnull org.springframework.data.domain.Sort sort) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<R> typedQuery = builder.createQuery(resultClass);
        Root<E> root = applySpecificationToCriteria(spec, domainClass, typedQuery);
        if (sort.isSorted()) {
            typedQuery.orderBy(QueryUtils.toOrders(sort, root, builder));
        }
        return entityManager.createQuery(typedQuery);
    }

    protected <E> TypedQuery<Long> getCountQuery(@Nonnull Specification<E> spec, @Nonnull Class<E> domainClass) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<E> root = query.from(domainClass);
        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }
        Predicate predicate = spec.toPredicate(root, query, builder);
        if (predicate != null) {
            query.where(predicate);
        }
        query.distinct(true);
        return entityManager.createQuery(query);
    }

    private <E, R extends Projection> Root<E> applySpecificationToCriteria(@Nonnull Specification<E> spec,
                                                                           @Nonnull Class<E> domainClass,
                                                                           @Nonnull CriteriaQuery<R> query) {
        Root<E> root = query.from(domainClass);
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Predicate predicate = spec.toPredicate(root, query, builder);
        if (predicate != null) {
            query.where(predicate);
        }
        query.distinct(true);
        return root;
    }

}
