package pu.jpa.dynamicquery.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pu.jpa.dynamicquery.model.entity.Customer;
import pu.jpa.dynamicquery.model.projection.CustomerWithCount;

/**
 * @author Plamen Uzunov
 */
@Repository
public interface CustomersRepository extends JpaRepository<Customer, Long>, SearchableRepository<CustomerWithCount> {

    @Override
    @Query(nativeQuery = true, value = """
            select c.id as id, c.name as name,
                   (select COUNT(cont.customer_id) from contacts cont where cont.customer_id = c.id group by cont.customer_id) as contactsCount
            from customers c
            group by id, name
        """)
    Page<CustomerWithCount> search(Pageable pageable);

}
