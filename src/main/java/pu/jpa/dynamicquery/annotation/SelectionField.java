package pu.jpa.dynamicquery.annotation;

/**
 * @author Plamen Uzunov
 */
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.PACKAGE, java.lang.annotation.ElementType.ANNOTATION_TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Inherited
public @interface SelectionField {

    /**
     * (Required) The type of the projection class.
     *
     * @return The type of the projection class
     */
    Class<?> type();

    /**
     * (Required) The parameter's name of the corresponding type projection mapped to
     * the annotated Entity's field in the SELECT clause of a SQL query.
     *
     * @return The parameter name of the corresponding type projection
     */
    String name();

}
