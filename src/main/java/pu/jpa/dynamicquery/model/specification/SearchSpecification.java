package pu.jpa.dynamicquery.model.specification;

import java.io.Serial;
import java.util.Map;

import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import pu.jpa.dynamicquery.api.Expression;
import pu.jpa.dynamicquery.model.projection.Projection;

/**
 * @author Plamen Uzunov
 */
@Data
public class SearchSpecification<E, V extends Projection> implements Specification<E> {

    @Serial
    private static final long serialVersionUID = 8261978006588865592L;

    private Predicate joinPredicate;
    private Expression expression;
    private JoinDataSupplier<E, V> joinDataSupplier;

    public SearchSpecification(final Class<E> domainClass, final Expression expression) {
        this.expression = expression;
        this.joinDataSupplier = new JoinDataSupplier<>() {
            @Override
            public Map<String, Join<Object, Object>> getJoinData(Root<E> root, CriteriaQuery<V> query, CriteriaBuilder criteriaBuilder) {
                try {
                    JoinTablesSelectionsBuilder<E, V> builder = new JoinTablesSelectionsBuilder<>(domainClass, expression, root, query, criteriaBuilder);
                    builder.processTables();
                    joinPredicate = builder.getJoinPredicate();
                    return builder.getMappedJoins();
                } catch (NoSuchFieldException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Predicate toPredicate(@Nonnull Root<E> root, CriteriaQuery<?> query, @Nonnull CriteriaBuilder criteriaBuilder) {
        if (joinDataSupplier != null && (expression != null || joinPredicate != null)) {
            Map<String, Join<Object, Object>> joinData = joinDataSupplier.getJoinData(root, (CriteriaQuery<V>) query, criteriaBuilder);
            Predicate expPredicate = expression.toPredicate(root, query, criteriaBuilder, joinData);
            return (joinPredicate == null) ? expPredicate : criteriaBuilder.and(joinPredicate, expPredicate);
        }
        return criteriaBuilder.conjunction();
    }


}
