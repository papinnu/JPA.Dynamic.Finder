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
@Table(name = "contacts")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contact implements Serializable {

    @Serial
    private static final long serialVersionUID = 7693189679943046272L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "serial")
    @JsonProperty(value = "id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Customer.class)
    @JoinColumn(name = "customer_id", nullable = false, insertable=false, updatable=false)
    private Customer customer;

    @Column(nullable = false, length = 50)
    @JsonProperty(value = "name")
    private String name;

    @Column(nullable = false, length = 16)
    @JsonProperty(value = "type")
    private String type;

    @Column(name = "value")
    private String value = null;

    @Column(name = "isprimary", nullable = false)
    private boolean primary = false;

}
