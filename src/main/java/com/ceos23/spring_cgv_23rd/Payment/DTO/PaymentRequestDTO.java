package com.ceos23.spring_cgv_23rd.Payment.DTO;

public record MoviePaymentRequestDTO(
        String storeId,
        String orderName,
        long totalPayAmount,
        Currency currency,
        String customData
) {
}
