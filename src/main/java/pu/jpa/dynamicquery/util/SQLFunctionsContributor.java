package pu.jpa.dynamicquery.util;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.type.StandardBasicTypes;
import pu.jpa.dynamicquery.api.SQLFunctionType;

/**
 * @author Plamen Uzunov
 */
public class SQLFunctionsContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        SqmFunctionRegistry registry = functionContributions.getFunctionRegistry();

        registry.registerPattern(
            "concatwithspace",
            "?1 || ' ' || ?2",
            functionContributions
                .getTypeConfiguration()
                .getBasicTypeRegistry()
                .resolve(StandardBasicTypes.STRING))
        ;
        registry.registerPattern(
            SQLFunctionType.BIT_AND.toString(),
            "(?1 & ?2)",
            functionContributions
                .getTypeConfiguration()
                .getBasicTypeRegistry()
                .resolve(StandardBasicTypes.INTEGER));
        registry.registerPattern(
            SQLFunctionType.BIT_OR.toString(),
            "(?1 | ?2)",
            functionContributions
                .getTypeConfiguration()
                .getBasicTypeRegistry()
                .resolve(StandardBasicTypes.INTEGER));
        registry.registerPattern(
            SQLFunctionType.BIT_XOR.toString(),
            "(?1 ^ ?2)",
            functionContributions
                .getTypeConfiguration()
                .getBasicTypeRegistry()
                .resolve(StandardBasicTypes.INTEGER));
    }

}
