package com.uit.garage.dao;

public class ReceiptDisplay {
    private final int id;
    private final String licensePlate;
    private final String receiptDate;
    private final String note;
    private final String status;

    public ReceiptDisplay(int id, String licensePlate, String receiptDate, String note, String status) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.receiptDate = receiptDate;
        this.note = note;
        this.status = status;
    }

    public int getId() { return id; }
    public String getLicensePlate() { return licensePlate; }
    public String getReceiptDate() { return receiptDate; }
    public String getNote() { return note; }
    public String getStatus() { return status; }
}
