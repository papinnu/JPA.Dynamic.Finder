package pu.jpa.dynamicquery.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation should be used into the {@code @Entity} types to define the properties to be added
 * dynamically to the target SQL query.
 *
 * @author Plamen Uzunov
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface SelectionIn {

    /**
     * Array of types where this filed is mapped to the certain (JPA) projection class with the defined name.
     *
     * @return Types array
     */
    SelectionField[] fields() default {};
    SQLFunction[] functions() default {};

}
