package pu.jpa.dynamicquery.model.projection;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pu.jpa.dynamicquery.api.Projection;

/**
 * @author Plamen Uzunov
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerWithCount implements Projection {

    private long id;
    private String name;
    private Long contactsCount;

}
