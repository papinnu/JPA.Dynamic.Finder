package pu.jpa.dynamicquery;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
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
import pu.jpa.dynamicquery.api.LogicalOperator;
import pu.jpa.dynamicquery.api.Projection;
import pu.jpa.dynamicquery.api.SortType;
import pu.jpa.dynamicquery.api.Sortable;
import pu.jpa.dynamicquery.model.entity.Product;
import pu.jpa.dynamicquery.model.entity.Substance;
import pu.jpa.dynamicquery.model.filter.AbstractExpression;
import pu.jpa.dynamicquery.model.filter.CompositeExpression;
import pu.jpa.dynamicquery.model.filter.Filter;
import pu.jpa.dynamicquery.model.filter.Pagination;
import pu.jpa.dynamicquery.model.filter.SimpleExpression;
import pu.jpa.dynamicquery.model.projection.ProductProjection;
import pu.jpa.dynamicquery.repository.ProductsRepository;
import pu.jpa.dynamicquery.repository.SearchableRepository;
import pu.jpa.dynamicquery.util.JpaQueryBuilder;

/**
 * @author Plamen Uzunov
 */
@ConfigurationPropertiesScan
@Transactional
@ContextConfiguration(classes = {JpaQueryBuilder.class})
@EnableAutoConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(locations = "classpath:application-tc.yml")
@ActiveProfiles("tc")
@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class JPADynamicQueryBuilderSpringBootTest {

    @Autowired
    private JpaQueryBuilder jpaQueryBuilder;

    @Autowired
    private EntityManager entityManager;

    private final ProductsRepository productsRepository;

    @Container
    private static final PostgreSQLContainer<TestPostgreSqlContainer> postgresqlContainer =
        TestPostgreSqlContainer.getInstance();

    @Autowired
    public JPADynamicQueryBuilderSpringBootTest(JpaQueryBuilder jpaQueryBuilder, ProductsRepository productsRepository) {
        this.jpaQueryBuilder = jpaQueryBuilder;
        this.productsRepository = productsRepository;
    }

    @Transactional
    @BeforeEach
    void setUp() {
        Substance substance = new Substance("Acetylsalicylic acid", "Acetylsalicylic acid (ASA) 100 mg/l");
        entityManager.persist(substance);
        entityManager.flush();

        Product product = new Product("Aspirin", "Aspirin protect", LocalDate.now(), substance);
        entityManager.persist(product);
        entityManager.flush();

        substance = new Substance("Ibuprofen", "Isobutylphenylpropionic acid");
        entityManager.persist(substance);
        entityManager.flush();

        product = new Product("Nurofen", "Nurofen forte", LocalDate.now(), substance);
        entityManager.persist(product);
        entityManager.flush();
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
    void testQueryBuilder() {
        Filter<String> substanceNameFilter = new Filter<>();
        substanceNameFilter.setName("substance.description");
        substanceNameFilter.setValue("acid");
        substanceNameFilter.setOperator(ComparisonOperator.CONTAINS);
        SimpleExpression substanceDescExpression = new SimpleExpression(substanceNameFilter);

        Filter<String> productNameFilter = new Filter<>();
        productNameFilter.setName("name");
        productNameFilter.setValue("rin");
        productNameFilter.setOperator(ComparisonOperator.ENDS_WITH);
        SimpleExpression productNameExpression = new SimpleExpression(productNameFilter);
        AbstractExpression expression = new CompositeExpression(LogicalOperator.AND, List.of(substanceDescExpression, productNameExpression));

        Pagination pagination = new Pagination();
        pagination.setFilter(expression);
        pagination.setSort(List.of(new pu.jpa.dynamicquery.model.filter.Sort("name", SortType.ASC)));

        Page<ProductProjection> page = search(pagination, Product.class, ProductProjection.class, productsRepository);
        List<ProductProjection> results = page.getContent();
        Assertions.assertEquals(1, results.size());
    }

    private <V extends Projection, E> Page<V> search(Pagination pagination, Class<E> entityClass, Class<V> viewClass, SearchableRepository<V> repository) {
        if (pagination.getFilter() != null) {
            return jpaQueryBuilder.getPagedData(entityClass, viewClass, pagination);
        } else {
            PageRequest pageReq = buildPageRequest(pagination);
            return repository.search(pageReq);
        }
    }

    private PageRequest buildPageRequest(Pagination pagination) {
        List<Sort.Order> orders = new ArrayList<>();
        List<Sortable> sortList = pagination.getSort();
        if (sortList != null && !sortList.isEmpty()) {
            sortList.forEach(sort -> orders.add(new Sort.Order(sort.getType().toDomainSort(), sort.getField())));
        }
        return PageRequest.of(pagination.getPage(), pagination.getPageSize(), Sort.by(orders));
    }

}