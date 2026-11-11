package com.daniel.customerplatform.customer.models;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    private UUID id;
    private Long version;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Instant createdAt;
    private Instant updatedAt;
    private CustomerStatus customerStatus;
    private String preferredTimeZone;
    private String language;
    private Boolean marketingOptIn;
    private Set<String> tags;
    private Collection<Address> addresses = new ArrayList<>();

//Helper methods

    public void addAddress(Address address) {
        this.addresses.add(address);
        address.setCustomer(this);
    }
}