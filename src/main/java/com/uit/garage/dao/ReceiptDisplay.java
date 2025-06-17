package com.uit.garage.dao;

public class ReceiptDisplay {
    private final int id;
    private final String licensePlate;
    private final String receiptDate;

    public ReceiptDisplay(int id, String licensePlate, String receiptDate) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.receiptDate = receiptDate;
    }

    public int getId() { return id; }
    public String getLicensePlate() { return licensePlate; }
    public String getReceiptDate() { return receiptDate; }
}
