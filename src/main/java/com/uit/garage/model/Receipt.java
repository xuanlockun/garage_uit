package com.uit.garage.model;

import java.time.LocalDate;

public class Receipt {
    private int id;
    private int vehicleId;
    private LocalDate receiptDate;
    private String note;

    public Receipt(int vehicleId, LocalDate receiptDate, String note) {
        this.vehicleId = vehicleId;
        this.receiptDate = receiptDate;
        this.note = note;
    }


    // Getters & Setters
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public LocalDate getReceiptDate() { return receiptDate; }
    public void setReceiptDate(LocalDate receiptDate) { this.receiptDate = receiptDate; }
}
