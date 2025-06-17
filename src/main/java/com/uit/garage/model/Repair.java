package com.uit.garage.model;

public class Repair {
    private int id;
    private int vehicleId;
    private String repairDate;
    private String content;
    private String part;
    private int quantity;
    private double unitPrice;
    private double laborCost;

    public Repair(int id, int vehicleId, String repairDate, String content, String part, int quantity, double unitPrice, double laborCost) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.repairDate = repairDate;
        this.content = content;
        this.part = part;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.laborCost = laborCost;
    }

    public Repair(int vehicleId, String repairDate, String content, String part, int quantity, double unitPrice, double laborCost) {
        this(0, vehicleId, repairDate, content, part, quantity, unitPrice, laborCost);
    }

    public int getId() {
        return id;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public String getRepairDate() {
        return repairDate;
    }

    public String getContent() {
        return content;
    }

    public String getPart() {
        return part;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getLaborCost() {
        return laborCost;
    }

    public double getTotalCost() {
        return quantity * unitPrice + laborCost;
    }

    // Setters nếu cần
}
