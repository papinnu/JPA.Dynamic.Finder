package pu.jpa.dynamicquery;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.TestcontainersConfiguration;
import pu.jpa.dynamicquery.api.ComparisonOperator;
import pu.jpa.dynamicquery.api.Expression;
import pu.jpa.dynamicquery.api.LogicalOperator;
import pu.jpa.dynamicquery.api.Pageable;
import pu.jpa.dynamicquery.api.Projection;
import pu.jpa.dynamicquery.api.SortType;
import pu.jpa.dynamicquery.api.Sortable;
import pu.jpa.dynamicquery.configuration.PageableConfig;
import pu.jpa.dynamicquery.model.entity.Customer;
import pu.jpa.dynamicquery.model.filter.CompositeExpression;
import pu.jpa.dynamicquery.model.filter.Filter;
import pu.jpa.dynamicquery.model.filter.Pagination;
import pu.jpa.dynamicquery.model.filter.SimpleExpression;
import pu.jpa.dynamicquery.model.projection.CustomerWithCount;
import pu.jpa.dynamicquery.repository.CustomersRepository;
import pu.jpa.dynamicquery.repository.SearchableRepository;
import pu.jpa.dynamicquery.util.JpaQueryBuilder;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Plamen Uzunov
 */
@ConfigurationPropertiesScan
@Transactional
@ContextConfiguration(classes = {JpaQueryBuilder.class})
@EnableAutoConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ComponentScan(basePackages = {"pu.jpa.dynamicquery.configuration", "pu.jpa.dynamicquery.util"})
@TestPropertySource(locations = "classpath:application-tc.yml")
@ActiveProfiles("tc")
@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class JPADynamicQueryBuilderSpringBootTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    PageableConfig pageableConfig;

    private final JpaQueryBuilder jpaQueryBuilder;

    private final CustomersRepository customersRepository;

    @Container
    private static final PostgreSQLContainer<TestPostgreSqlContainer> postgresqlContainer =
        TestPostgreSqlContainer.getInstance();

    @Autowired
    public JPADynamicQueryBuilderSpringBootTest(JpaQueryBuilder jpaQueryBuilder, CustomersRepository customersRepository) {
        this.jpaQueryBuilder = jpaQueryBuilder;
        this.customersRepository = customersRepository;
    }

    @Transactional
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    @Transactional
    @Test
    void testCustomersAPI() {
        Filter<String> contactNameFilter = new Filter<>();
        contactNameFilter.setName("contacts.name");
        contactNameFilter.setValue("a");
        contactNameFilter.setOperator(ComparisonOperator.CONTAINS);
        Expression contactNameExpression = new SimpleExpression(contactNameFilter);

        Filter<String> customerNameFilter = new Filter<>();
        customerNameFilter.setName("name");
        customerNameFilter.setValue("ltd.");
        customerNameFilter.setOperator(ComparisonOperator.ENDS_WITH);
        SimpleExpression productNameExpression = new SimpleExpression(customerNameFilter);
        Expression expression = new CompositeExpression(LogicalOperator.AND, List.of(contactNameExpression, productNameExpression));

        Pageable pagination = pageableConfig.pageableBuilder()
            .withFilter(expression)
            .withSort(List.of(new pu.jpa.dynamicquery.model.filter.Sort("name", SortType.ASC)))
            .build();

        Page<CustomerWithCount> page = search(pagination, Customer.class, CustomerWithCount.class, customersRepository);
        List<CustomerWithCount> results = page.getContent();
        Assertions.assertEquals(4, results.size());
    }

    @Transactional
    @Test
    void testCustomersJsonPayload() {
        Pageable pagination = deserialisePagination(getCustomerSearchJsonStream("customer.search.json"));
        assertNotNull(pagination);

        Page<CustomerWithCount> page = search(pagination, Customer.class, CustomerWithCount.class, customersRepository);
        List<CustomerWithCount> results = page.getContent();
        Assertions.assertEquals(5, results.size());
    }

    @Transactional
    @Test
    void testCustomersJsonPayloadPagination() {
        Pageable pagination = deserialisePagination(getCustomerSearchJsonStream("customer.search_pagination.json"));
        assertNotNull(pagination);

        Page<CustomerWithCount> page = search(pagination, Customer.class, CustomerWithCount.class, customersRepository);
        List<CustomerWithCount> results = page.getContent();
        Assertions.assertEquals(2, results.size());
    }

    @Transactional
    @Test
    void testCustomersJsonPayloadContactsSearch() {
        Pageable pagination = deserialisePagination(getCustomerSearchJsonStream("customer.search_contact.json"));
        assertNotNull(pagination);

        Page<CustomerWithCount> page = search(pagination, Customer.class, CustomerWithCount.class, customersRepository);
        List<CustomerWithCount> results = page.getContent();
        Assertions.assertEquals(2, results.size());
    }

    @SneakyThrows({JsonParseException.class, IOException.class})
    private Pagination deserialisePagination(InputStream json) {
        return objectMapper.readValue(json, Pagination.class);
    }

    private <V extends Projection, E> Page<V> search(Pageable pagination, Class<E> entityClass, Class<V> viewClass, SearchableRepository<V> repository) {
        if (pagination.getFilter() != null) {
            return jpaQueryBuilder.getPagedData(entityClass, viewClass, pagination);
        } else {
            PageRequest pageReq = buildPageRequest(pagination);
            return repository.search(pageReq);
        }
    }

    private PageRequest buildPageRequest(Pageable pagination) {
        List<Sort.Order> orders = new ArrayList<>();
        List<Sortable> sortList = pagination.getSort();
        if (sortList != null && !sortList.isEmpty()) {
            sortList.forEach(sort -> orders.add(new Sort.Order(sort.getType().toDomainSort(), sort.getField())));
        }
        return PageRequest.of(pagination.getPage(), pagination.getPageSize(), Sort.by(orders));
    }

    private InputStream getCustomerSearchJsonStream(String payloadFileName) {
        return JPADynamicQueryBuilderSpringBootTest.class.getClassLoader().getResourceAsStream("json/" + payloadFileName);
    }

}