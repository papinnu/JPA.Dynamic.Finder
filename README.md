# JPA.Dynamic.Finder

### Example how to use it:

 ```java 
JpaQueryBuilder jpaQueryBuilder = new JpaQueryBuilder();
Page<MyProjection> page = jpaQueryBuilder.getPagedData(MyEntity.class, MyProjection.class, pagination);
 ```
Where the `MyEntity` class is a domain class annotated with the `jakarta.persistence.Entity` - this is a
data source class for the builder.
Next, the `MyProjection` class is an interface, class or record DTO  representation which extends or 
implements the empty defined `Projection` interface. This class is a result data representation. 
The `pagination` is a `Pagination` instance which represents finder criteria data, like: filter criteria 
data, page size, page number and list of sorted columns.

#### How to build a pagination with filter to search for.
by code :
 ```java 
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
 ```
or by JSON data from remote callers:
```javascript
{
    "pagination": {
        "expression": {
            "and": [
                {
                    "name": "name",
                    "method": 5,
                    "val": "rin"
                },
                {
                    "name": "substance.name",
                    "method": 3,
                    "val": "acid"
                }
            ]
        },
        "number": 0,
            "size": 10,
            "sort": [
            {
                "field": "name",
                "type": "ASC"
            }
        ]
    }
}
```