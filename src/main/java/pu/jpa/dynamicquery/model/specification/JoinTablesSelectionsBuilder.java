package pu.jpa.dynamicquery.model.specification;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import lombok.Getter;
import pu.jpa.dynamicquery.annotation.SQLFunction;
import pu.jpa.dynamicquery.annotation.SelectionIn;
import pu.jpa.dynamicquery.api.Condition;
import pu.jpa.dynamicquery.api.Expression;
import pu.jpa.dynamicquery.model.projection.Projection;

/**
 * @author Plamen Uzunov
 */
public class JoinTablesSelectionsBuilder<E, V extends Projection> {

    @Getter
    private final Map<String, Join<Object, Object>> mappedJoins = new LinkedHashMap<>();
    private final Map<String, Class<?>> entities = new LinkedHashMap<>();
    private final Map<String, Selection<?>> selections = new HashMap<>();
    private final Root<?> root;
    private final CriteriaQuery<V> query;
    private final CriteriaBuilder builder;
    private final Class<E> parentEntityType;
    private final Expression expression;

    @Getter
    private Predicate joinPredicate;
    private boolean aggregation = false;
    private final Map<String, jakarta.persistence.criteria.Expression<?>> grouping = new HashMap<>();

    public JoinTablesSelectionsBuilder(Class<E> parentEntityType,
                                       Expression expression,
                                       Root<?> root,
                                       CriteriaQuery<V> query,
                                       CriteriaBuilder builder) {
        this.root = root;
        this.query = query;
        this.builder = builder;
        this.parentEntityType = parentEntityType;
        this.expression = expression;
    }

    public void processTables() throws NoSuchFieldException {
        boolean processSelections = query.getSelection() == null
            || query.getSelection().getJavaType() == null
            || Projection.class.isAssignableFrom(query.getSelection().getJavaType());
        if (processSelections) {
            collectSelections("", parentEntityType, root);
        }
        if (expression != null) {
            List<Condition<?>> filters = expression.getConditions();
            for (Condition<?> filter : filters) {
                processFilter(filter, null, filter.getName(), parentEntityType, root);
            }
        }
        if (processSelections) {
            List<Selection<?>> selectionsList = orderSelections(query.getResultType());
            query.multiselect(selectionsList);
        }
        if (aggregation) {
            query.groupBy(new ArrayList<>(grouping.values()));
        }
    }

    private void collectSelections(@Nonnull String name, @Nonnull Class<?> entityType, @Nonnull From<?, ?> currentJoin) {
        if (entityType.isAnnotationPresent(SelectionIn.class)) {
            SelectionIn selectionIn = entityType.getAnnotation(SelectionIn.class);
            //process function(s)
            if (selectionIn.functions() != null) {
                for (SQLFunction sqlFunction : selectionIn.functions()) {
                    From<?, ?> currentRoot = currentJoin;
                    if (sqlFunction.type().equals(query.getResultType())) {
                        jakarta.persistence.criteria.Expression<?>[] exp = new jakarta.persistence.criteria.Expression<?>[sqlFunction.expressions().length];
                        if (sqlFunction.joins() != null) {
                            for (pu.jpa.dynamicquery.annotation.Join join : sqlFunction.joins()) {
                                currentRoot = query.from(join.join());
                                if (!join.attribute().isEmpty()) {
                                    joinTable(join.attribute(), currentRoot, join.type());
                                } else if (!join.joinColumn().isEmpty()) {
                                    jakarta.persistence.criteria.Expression<?> joinExp = currentRoot.get(join.joinColumn());
                                    if (joinPredicate == null) {
                                        joinPredicate = builder.equal(root.get("id"), joinExp);
                                    } else {
                                        joinPredicate = builder.and(joinPredicate, builder.equal(root.get("id"), joinExp));
                                    }
                                }
                            }
                        }
                        if (!sqlFunction.function().isEmpty()) {
                            for (int i = 0; i < sqlFunction.expressions().length; i++) {
                                String expression = sqlFunction.expressions()[i];
                                String[] paths = expression.split("\\.");
                                exp[i] = tryToJoin(paths, currentRoot, currentJoin);
                                grouping.put(paths[paths.length - 1], exp[i]);
                            }
                            jakarta.persistence.criteria.Expression<?> function = builder.function(sqlFunction.function(), sqlFunction.resultType(), exp);
                            Selection<?> selection = function.alias(sqlFunction.name());
                            selections.put(sqlFunction.name().toLowerCase(), selection);
                            if (sqlFunction.aggregation()) {
                                aggregation = true;
                            }
                        }
                    }
                }
            }
        }
        //process fields
        collectSelections(name, entityType.getDeclaredFields(), currentJoin);
    }

    private void collectSelections(final @Nonnull String name, @Nonnull Field[] fields, @Nonnull From<?, ?> currentJoin) {
        for (Field field : fields) {
            if (field.isAnnotationPresent(SelectionIn.class)) {
                SelectionIn selectionIn = field.getAnnotation(SelectionIn.class);
                //process fields
                if (selectionIn.fields() != null && selectionIn.fields().length > 0) {
                    Arrays.stream(selectionIn.fields())
                        .filter(selectionField -> selectionField.type().equals(query.getResultType()))
                        .findFirst()
                        .ifPresent(selectionField -> {
                            Class<?> entity = getEntityClass(field);
                            String currentName = name.toLowerCase() + selectionField.name().toLowerCase();
                            if (entity != null) {
                                From<?, ?> join = joinTable(field.getName(), currentJoin);
                                entities.putIfAbsent(field.getName(), entity);
                                collectSelections(currentName, entity, join);
                            } else {
                                jakarta.persistence.criteria.Expression<?> selectionExp = currentJoin.get(field.getName());
                                Selection<?> selection = selectionExp.alias(currentName);
                                selections.put(currentName, selection);
                                grouping.put(currentName, selectionExp);
                            }
                        });
                }
            }
        }
    }

    private Class<?> getEntityClass(Field field) {
        Class<?> entity = null;
        if (Collection.class.isAssignableFrom(field.getType())) {
            // Map<?,?> is not supported
            Class<?> type = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            if (type.isAnnotationPresent(jakarta.persistence.Entity.class)) {
                entity = type;
            }
        } else if (field.getType().isAnnotationPresent(jakarta.persistence.Entity.class)) {
            entity = field.getType();
        }
        return entity;
    }

    private From<?, ?> joinTable(String fieldName, From<?, ?> from) {
        return joinTable(fieldName, from, JoinType.INNER);
    }

    private From<?, ?> joinTable(String fieldName, From<?, ?> from, JoinType type) {
        Join<Object, Object> join = mappedJoins.get(fieldName);
        if (join == null) {
            join = from.join(fieldName, type);
            mappedJoins.put(fieldName, join);
        }
        return join;
    }

    private void processFilter(Condition<?> filter, String parent, String path, Class<?> entityType, From<?, ?> parentJoin) throws NoSuchFieldException {
        String[] paths = path.split("\\.", 2);
        if (paths.length == 1) {
            if (parent == null || parent.isBlank()) {
                filter.setEntity(entityType);
                filter.setMappedName(path);
            } else {
                filter.setEntity(entities.get(parent));
                filter.setMappedName(parent);
            }
        } else {
            Field field = entityType.getDeclaredField(paths[0]);
            Class<?> entityClass = getEntityClass(field);
            if (entityClass != null) {
                entities.putIfAbsent(field.getName(), entityClass);
            }
            From<?, ?> join = joinTable(field.getName(), parentJoin);
            processFilter(filter, paths[0], paths[1], entityClass, join);
        }
    }

    private List<Selection<?>> orderSelections(Class<V> viewClass) {
        return Arrays.stream(viewClass.getDeclaredConstructors())
            .filter(constructor -> constructor.getParameterCount() == selections.size())
            .findFirst().map(viewConstructor -> {
                Parameter[] params = viewConstructor.getParameters();
                List<Selection<?>> result = new ArrayList<>();
                for (Parameter param : params) {
                    Selection<?> selection = selections.get(param.getName().toLowerCase());
                    if (selection == null) {
                        throw new RuntimeException("No selection found for parameter " + param.getName());
                    }
                    result.add(selection);
                }
                return result;
            })
            .orElseThrow(() -> new IllegalArgumentException(String.format("No constructor found with the parameters count '%1$d' for the projection class: '%2$s'", selections.size(), viewClass.getName())));
    }

    private jakarta.persistence.criteria.Expression<?> tryToJoin(String[] paths,
                                                                 @Nonnull From<?, ?> currentRoot,
                                                                 @Nonnull From<?, ?> currentJoin) {
        if (paths == null || paths.length == 0) {
            return null;
        }
        jakarta.persistence.criteria.Expression<?> currentExp;
        if (paths.length == 1) {
            currentExp = tryToJoin(paths[0], currentRoot, currentJoin);
        } else {
            if (paths.length == 2) {
                currentExp = tryToJoin(paths[1], currentRoot, currentJoin);
                if (currentExp == null) {
                    From<?, ?> join = joinTable(paths[1], currentJoin);
                    currentExp = join.get(paths[1]);
                }
            } else {
                currentExp = tryToJoin(paths[2], currentRoot, currentJoin);
                if (currentExp == null) {
                    From<?, ?> join = joinTable(paths[1], currentJoin);
                    currentExp = join.get(paths[2]);
                }
            }
        }
        return currentExp;
    }

    private jakarta.persistence.criteria.Expression<?> tryToJoin(String joinColumn,
                                                                 @Nonnull From<?, ?> currentRoot,
                                                                 @Nonnull From<?, ?> currentJoin) {
        jakarta.persistence.criteria.Expression<?> res = null;
        try {
            res = currentRoot.get(joinColumn);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            try {
                res = currentJoin.get(joinColumn);
            } catch (IllegalArgumentException | IllegalStateException ex1) {
                //empty
            }
        }
        return res;
    }

}
