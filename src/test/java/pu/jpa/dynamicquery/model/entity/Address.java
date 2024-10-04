package pu.jpa.dynamicquery.model.entity;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "addresses")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address implements Serializable {

    @Serial
    private static final long serialVersionUID = -5859284827229063617L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "serial")
    @JsonProperty(value = "id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Customer.class)
    @JoinColumn(name = "customer_id", nullable = false, insertable=false, updatable=false)
    private Customer customer;

    @Column(name = "city", nullable = false, length = 50)
    private String city = null;

    @Column(name = "zip", nullable = false, length = 12)
    private String zip = null;

    @Column(nullable = false, length = 196)
    private String address = null;

    @Column(name = "isprimary", nullable = false)
    private boolean primary = false;

}
