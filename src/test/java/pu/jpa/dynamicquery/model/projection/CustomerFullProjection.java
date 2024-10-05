package pu.jpa.dynamicquery.model.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import pu.jpa.dynamicquery.api.Projection;

/**
 * @author Plamen Uzunov
 */
@Getter
@Setter
public class CustomerFullProjection implements Projection {

    long id;
    String company;
    @JsonProperty(namespace = "contact")
    private ContactProjection contact;
    @JsonProperty(namespace = "address")
    private AddressProjection address;

    public CustomerFullProjection(long id, String company, long contactId, String contactName, String contactType, String contactValue,
                                  long addressId, String addressAddress, String addressCityName, String addressCityStateName, String addressZip,
                                  String addressCityStateCountryName) {
        this.id = id;
        this.company = company;
        this.contact = new ContactProjection(contactId, contactName, contactType, contactValue);
        this.address = new AddressProjection(addressId, addressAddress, addressCityName, addressCityStateName, addressZip, addressCityStateCountryName);
    }

}
