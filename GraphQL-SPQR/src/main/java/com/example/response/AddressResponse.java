package com.example.response;

import com.example.dto.AddressDTO;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.types.GraphQLType;
import lombok.Getter;
import lombok.Setter;

@GraphQLType
@Getter
@Setter
public class AddressResponse {

    private String street;
    private String city;

    public AddressResponse(AddressDTO address) {
        this.street = address.getStreet();
        this.city = address.getCity();
    }
}