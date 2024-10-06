# JPA.Dynamic.Finder

### Example how to use it:

 ```java 
JpaQueryBuilder jpaQueryBuilder = new JpaQueryBuilder();
Page<MyProjection> page = jpaQueryBuilder.getPagedData(MyEntity.class, MyProjection.class, pagination);
 ```
Where the `MyEntity` class is a domain class annotated with the `jakarta.persistence.Entity` - it is a
data source class for the builder.
Next, the `MyProjection` class is an interface, class or record DTO representation which extends or 
implements the empty defined `Projection` interface. This class is a result data representation. 
The `pagination` is a `Pagination` instance which represents finder criteria data, like: filter criteria 
data, page size, page number and list of sorted columns.

#### How to build a pagination with filter to search for.
by API usage :
 ```java 
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

Pageable pagination = PageableBuilder.create()
    .withFilter(expression)
    .withSort(List.of(new pu.jpa.dynamicquery.model.filter.Sort("name", SortType.ASC)))
    .build();

Page<CustomerWithCount> page = jpaQueryBuilder.getPagedData(Customer.class, CustomerWithCount.class, pagination);
List<CustomerWithCount> results = page.getContent();
org.springframework.data.domain.Page pageResult = new org.springframework.data.domain.PageImpl<>(results);
 ```
or by JSON payload from remote callers:
```javascript
{
    "pagination": {
        "expression": {
            "and": [
                {
                    "name": "name",
                    "method": 3, // id value '3' means 'contains' - see `ComparisonOperator`
                    "val": "com"
                }
            ]
        },
        "number": 1,
            "size": 2,
            "sort": [
            {
                "field": "name",
                "type": "ASC"
            }
        ]
    }
}
```
Consuming it with the API:
```java
private static final String HEADER_PAGINATION_COUNT = "MyCustomHeader-Pagination-Count";
private static final String HEADER_PAGINATION_PAGE_NUMBER = "MyCustomHeader-Pagination-Page";
private static final String HEADER_PAGINATION_LIMIT = "MyCustomHeader-Pagination-Limit";

public ResponseEntity<List<CustomerWithCount>> search(
        @Parameter(in = ParameterIn.DEFAULT,
            description = "Pageable - filters, sorting columns, page number, page size",
            schema = @Schema(implementation = Pageable.class))
        @RequestBody Pageable body) throws ValidationException {
    Page<CustomerWithCount> page = jpaQueryBuilder.getPagedData(Customer.class, CustomerWithCount.class, body);
    return ResponseEntity.ok()
        .header(ACCESS_CONTROL_EXPOSE_HEADERS, String.join(", ", HEADER_PAGINATION_COUNT, HEADER_PAGINATION_PAGE_NUMBER, HEADER_PAGINATION_LIMIT))
        .header(HEADER_PAGINATION_COUNT, Long.toString(page.getTotalElements()))
        .header(HEADER_PAGINATION_PAGE_NUMBER, Integer.toString(page.getNumber()))
        .header(HEADER_PAGINATION_LIMIT, Long.toString(page.getNumberOfElements()))
        .body(page.getContent());
}
```