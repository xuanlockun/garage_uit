package com.uit.garage.model;

public class InvoiceRow {
    private int stt;
    private String licensePlate;
    private String brand;
    private String customerName;
    private double totalAmount;

    public InvoiceRow(int stt, String licensePlate, String brand, String customerName, double totalAmount) {
        this.stt = stt;
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
    }

    public int getStt() {
        return stt;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getBrand() {
        return brand;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

}
