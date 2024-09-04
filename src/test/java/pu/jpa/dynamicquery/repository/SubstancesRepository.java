package pu.jpa.dynamicquery.repository;

import org.springframework.stereotype.Repository;
import pu.jpa.dynamicquery.model.entity.Substance;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Plamen Uzunov
 */
@Repository
public interface SubstancesRepository extends JpaRepository<Substance, Long> {
}
