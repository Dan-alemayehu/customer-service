package com.daniel.customerplatform.customer.models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private Long id;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String countryCode;
    private Boolean primaryIndicator;
    private Customer customer;
}