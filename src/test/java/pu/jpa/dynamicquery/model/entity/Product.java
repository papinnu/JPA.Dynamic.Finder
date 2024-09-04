package pu.jpa.dynamicquery.model.entity;

import java.time.LocalDate;
import java.util.Objects;

import pu.jpa.dynamicquery.annotation.SelectionField;
import pu.jpa.dynamicquery.model.projection.ProductProjection;
import pu.jpa.dynamicquery.annotation.SelectionIn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Plamen Uzunov
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @SelectionIn(fields = {
        @SelectionField(name = "id", type = ProductProjection.class)
    })
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @SelectionIn(fields = {
        @SelectionField(name = "name", type = ProductProjection.class)
    })
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @SelectionIn(fields = {
        @SelectionField(name = "createdAt", type = ProductProjection.class)
    })
    @Column(name = "created_at")
    private LocalDate createdAt;

    @SelectionIn(fields = {
        @SelectionField(name = "substance", type = ProductProjection.class)}
    )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substanceId", referencedColumnName = "id")
    private Substance substance;

    public Product(String name,
                   String description,
                   LocalDate createdAt,
                   Substance substance) {
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.substance = substance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        if(id != null || product.getId() != null) {
            return Objects.equals(id, product.id);
        }
        if (!Objects.equals(name, product.name)) return false;
        if (!Objects.equals(description, product.description)) return false;
        if (!Objects.equals(createdAt, product.createdAt)) return false;
        return Objects.equals(substance, product.substance);
    }

    @Override
    public int hashCode() {
        if(id != null) {
            return id.hashCode();
        }
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (substance != null ? substance.hashCode() : 0);
        return result;
    }

}
