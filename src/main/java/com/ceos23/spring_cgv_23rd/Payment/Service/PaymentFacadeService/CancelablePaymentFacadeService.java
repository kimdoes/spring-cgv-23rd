package com.ceos23.spring_cgv_23rd.Payment.Service.PaymentService;

public interface CancelablePaymentService extends PaymentService{
    void cancel(long targetId);
}