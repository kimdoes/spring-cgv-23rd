package com.ceos23.spring_cgv_23rd.Payment.Service.PaymentService;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class PaymentIdHandler {
    private static long id = 20;

    public synchronized String getPaymentId(){
        id++;
        System.out.println("id plus >>> " + id);
        String localDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return localDate+"_" + String.format("%04d",id);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public synchronized void reset(){
        id = 0;
    }

}