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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pu.jpa.dynamicquery.api.Pageable;
import pu.jpa.dynamicquery.api.Projection;
import pu.jpa.dynamicquery.api.Sortable;
import pu.jpa.dynamicquery.model.specification.SearchSpecification;

/**
 * @author Plamen Uzunov
 */
@Component
public class JpaQueryBuilder {

    @PersistenceContext
    private EntityManager entityManager;

    public <E, R extends Projection> Page<R> getPagedData(@Nonnull Class<E> domainClass,
                                                          @Nonnull Class<R> resultClass,
                                                          @Nonnull Pageable paging) {
        Specification<E> specification = new SearchSpecification<>(domainClass, paging.getFilter());
        PageRequest request = getPageRequest(paging);
        TypedQuery<R> typedQuery = getTypedQuery(specification, domainClass, resultClass, request.getSort());
        return request.isUnpaged() ? new PageImpl<>(typedQuery.getResultList()) : getPage(typedQuery, domainClass, request, specification);
    }

    private <E, R extends Projection> Page<R> getPage(@Nonnull TypedQuery<R> query,
                                                     @Nonnull Class<E> domainClass,
                                                     @Nonnull org.springframework.data.domain.Pageable pageable,
                                                     @Nonnull Specification<E> spec) {
        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }
        return PageableExecutionUtils.getPage(query.getResultList(), pageable, () -> executeCountQuery(getCountQuery(spec, domainClass)));
    }

    private long executeCountQuery(@Nonnull TypedQuery<Long> countQuery) {
        List<Long> totals = countQuery.getResultList();
        long total = 0L;
        for (Long element : totals) {
            total += element == null ? 0 : element;
        }
        return total;
    }

    private PageRequest getPageRequest(Pageable paging) {
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

    private <E, R extends Projection> TypedQuery<R> getTypedQuery(@Nonnull Specification<E> spec,
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

    private <E> TypedQuery<Long> getCountQuery(@Nonnull Specification<E> spec, @Nonnull Class<E> domainClass) {
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
