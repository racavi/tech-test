package com.rafacalvo.prices.presentation;

public class ApiExamples {

    public static final String EXAMPLE_PRICE_RESPONSE = """
            {
              "productId": 12345,
              "brandId": 5,
              "priceList": 1,
              "startDate": "2025-03-24T00:00:00",
              "endDate": "2025-03-31T23:59:59",
              "price": 99.5,
              "currency": "GBP"
            }
            """;

    private ApiExamples() {
        // Private constructor to prevent instantiation
    }
}