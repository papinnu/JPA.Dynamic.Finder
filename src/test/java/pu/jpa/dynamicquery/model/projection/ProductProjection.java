package pu.jpa.dynamicquery.model.projection;

import java.time.LocalDate;

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
public class ProductProjection implements Projection {

    private long id;
    private String name;
    private LocalDate createdAt;
    private NameProjection substance;

    public ProductProjection(long id, String name, LocalDate createdAt, long substanceId, String substanceName) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.substance = new NameProjection(substanceId, substanceName);
    }
}
