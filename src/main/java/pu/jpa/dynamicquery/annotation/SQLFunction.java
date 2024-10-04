package pu.jpa.dynamicquery.annotation;

/**
 * This annotation should be used into the {@code @Selection} annotation to define usage of the JPA SQL function
 * when a SQL query is going to build dynamically. It can be used over the {@code @Entity} type only.
 *
 * @author Plamen Uzunov
 */
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.PACKAGE, java.lang.annotation.ElementType.ANNOTATION_TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Inherited
public @interface SQLFunction {

    /**
     * (Required) The type of the projection class.
     *
     * @return The type of the projection class
     */
    Class<?> type();

    Join[] joins() default {};

    /**
     * (Required) The parameter's name of the corresponding type projection mapped to
     * the annotated Entity's field in the SELECT clause of a SQL query.
     *
     * @return The parameter name of the corresponding type projection
     */
    String name();

    /**
     * The JPA SQL function name to apply over the annotated field.
     *
     * @return The name of the JPA SQL function
     */
    String function() default "";

    /**
     * The JPA SQL function result type.
     *
     * @return The type of the JPA SQL function
     */
    Class<?> resultType() default Integer.class;

    /**
     * Indicates whether the function is aggregation or not.
     *
     * @return the function is aggregation or not
     */
    boolean aggregation() default false;

    /**
     * The column's expression which will be used by this function
     *
     * @return the column's expression which will be used by this function
     */
    String[] expressions() default {};

}
