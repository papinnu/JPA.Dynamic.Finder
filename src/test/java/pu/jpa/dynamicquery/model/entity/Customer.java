package pu.jpa.dynamicquery.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.JoinType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pu.jpa.dynamicquery.annotation.Join;
import pu.jpa.dynamicquery.annotation.SQLFunction;
import pu.jpa.dynamicquery.annotation.SelectionField;
import pu.jpa.dynamicquery.annotation.SelectionIn;
import pu.jpa.dynamicquery.model.projection.CustomerFullProjection;
import pu.jpa.dynamicquery.model.projection.CustomerWithCount;

/**
 * @author Plamen Uzunov
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@SelectionIn(functions = {
    @SQLFunction(
        function = "count",
        name = "contactsCount",
        type = CustomerWithCount.class,
        resultType = Long.class,
        aggregation = true,
        expressions = {"Contact.id"},
        joins = @Join(attribute = "contacts", join = Contact.class, type = JoinType.LEFT)
    )
})
public class Customer implements Serializable {

    @Serial
    private static final long serialVersionUID = -1860229280777348932L;

    @SelectionIn(fields = {
        @SelectionField(name = "id", type = CustomerWithCount.class),
        @SelectionField(name = "id", type = CustomerFullProjection.class),
    })
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "serial")
    @JsonProperty(value = "id")
    private long id;

    @SelectionIn(fields = {
        @SelectionField(name = "name", type = CustomerWithCount.class),
        @SelectionField(name = "name", type = CustomerFullProjection.class),
    })
    @Column(nullable = false, length = 96)
    @JsonProperty(value = "name")
    private String name;

    @SelectionIn(fields = {
        @SelectionField(name = "contacts", type = CustomerFullProjection.class),
    })
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = Contact.class)
    @JoinColumn(name = "customer_id", nullable = false, insertable = false, updatable = false)
    private List<Contact> contacts = null;

    @SelectionIn(fields = {
        @SelectionField(name = "address", type = CustomerFullProjection.class),
    })
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = Address.class)
    @JoinColumn(name = "customer_id", nullable = false, insertable = false, updatable = false)
    private List<Address> addresses = null;

}
