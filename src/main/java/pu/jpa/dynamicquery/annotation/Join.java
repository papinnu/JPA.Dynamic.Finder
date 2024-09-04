package pu.jpa.dynamicquery.annotation;

import jakarta.persistence.criteria.JoinType;

/**
 * @author Plamen Uzunov
 */
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.PACKAGE, java.lang.annotation.ElementType.ANNOTATION_TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Inherited
public @interface Join {

    /**
     * (Required) The join entity class to the annotated entity have to be joined.
     *
     * @return The join type of entity to join with the annotated entity
     */
    Class<?> join();

    /**
     * The join column to the annotated entity have to be joined.
     *
     * @return The join column of entity to join with the annotated entity
     */
    String joinColumn() default "";

    /**
     * The entity attribute to be used to build custom join by JoinType.
     *
     * @return The entity attribute which will be used to build custom join by the given JoinType.
     */
    String attribute() default "";

    /**
     * The join type to be joined.
     *
     * @return The join type to join with the annotated entity
     */
    JoinType type() default JoinType.INNER;

}
