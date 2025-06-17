package com.uit.garage.model;

import java.time.LocalDate;

public class Receipt {
    private int id;
    private int vehicleId;
    private LocalDate receiptDate;

    public Receipt(int vehicleId, LocalDate receiptDate) {
        this.vehicleId = vehicleId;
        this.receiptDate = receiptDate;
    }

    public Receipt(int id, int vehicleId, LocalDate receiptDate) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.receiptDate = receiptDate;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public LocalDate getReceiptDate() { return receiptDate; }
    public void setReceiptDate(LocalDate receiptDate) { this.receiptDate = receiptDate; }
}
