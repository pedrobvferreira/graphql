package com.example.response;

import com.example.dto.AddressDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponse {
    private Long id;
    private String street;
    private String city;

    public AddressResponse(AddressDTO address) {
        this.street = address.getStreet();
        this.city = address.getCity();
    }
}