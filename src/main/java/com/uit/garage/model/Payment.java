package com.uit.garage.model;

import java.time.LocalDate;

public class Payment {
    private int id;
    private int invoiceId;
    private LocalDate paymentDate;
    private double amount;

    public Payment(int id, int invoiceId, LocalDate paymentDate, double amount) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.paymentDate = paymentDate;
        this.amount = amount;
    }

    public Payment(int invoiceId, LocalDate paymentDate, double amount) {
        this(-1, invoiceId, paymentDate, amount);
    }

    public int getId() {
        return id;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
