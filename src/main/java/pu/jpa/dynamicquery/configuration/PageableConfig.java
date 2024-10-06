package pu.jpa.dynamicquery.configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pu.jpa.dynamicquery.model.filter.Pagination;
import pu.jpa.dynamicquery.util.ExpressionDeserializer;
import pu.jpa.dynamicquery.util.PageableBuilder;

/**
 * @author Plamen Uzunov
 */
@Configuration
public class PageableConfig {

    @Autowired
    private PaginationRecord paginationRecord;

    @Bean
    public Module customDeserializerModule(ExpressionDeserializer expressionDeserializer) {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Pagination.class, expressionDeserializer);
        return module;
    }

    @Bean
    public ExpressionDeserializer expressionDeserializer() {
        return new ExpressionDeserializer(paginationRecord);
    }

    @Bean
    public PageableBuilder pageableBuilder() {
        return new PageableBuilder(paginationRecord);
    }

}
