package pu.jpa.dynamicquery.repository;

import pu.jpa.dynamicquery.api.Projection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author Plamen Uzunov
 */
public interface SearchableRepository<V extends Projection> {

    Page<V> search(Pageable pageable);

}
