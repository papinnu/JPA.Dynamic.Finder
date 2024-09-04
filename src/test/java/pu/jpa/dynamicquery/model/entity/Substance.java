package pu.jpa.dynamicquery.model.entity;

import java.util.Objects;

import pu.jpa.dynamicquery.annotation.SelectionField;
import pu.jpa.dynamicquery.model.projection.NameProjection;
import pu.jpa.dynamicquery.model.projection.ProductProjection;
import pu.jpa.dynamicquery.annotation.SelectionIn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Plamen Uzunov
 */
@Entity
@Table(name = "substances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Substance {

    @SelectionIn(fields = {
        @SelectionField(name = "id", type = NameProjection.class),
        @SelectionField(name = "id", type = ProductProjection.class)
    })
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @SelectionIn(fields = {
        @SelectionField(name = "name", type = NameProjection.class),
        @SelectionField(name = "name", type = ProductProjection.class)
    })
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    public Substance(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Substance substance)) return false;
        if(id != null || substance.getId() != null) {
            return Objects.equals(id, substance.id);
        }
        if (!Objects.equals(name, substance.name)) return false;
        return Objects.equals(description, substance.description);
    }

    @Override
    public int hashCode() {
        if(id != null) {
            return id.hashCode();
        }
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
