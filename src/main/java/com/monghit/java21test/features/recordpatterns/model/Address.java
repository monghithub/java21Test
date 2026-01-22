package com.monghit.java21test.features.recordpatterns.model;

/**
 * Record que representa una dirección.
 *
 * Usado para demostrar Record Patterns y deconstrucción anidada.
 */
public record Address(
    String street,
    String city,
    String zipCode,
    String country
) {
    public Address {
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street cannot be null or empty");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
    }
}
