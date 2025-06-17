package com.uit.garage.model;

public class Vehicle {
    private int id;
    private String licensePlate;
    private String brand;
    private int customerId;

    public Vehicle(String licensePlate, String brand, int customerId) {
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.customerId = customerId;
    }

    public Vehicle(int id, String licensePlate, String brand, int customerId) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.customerId = customerId;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
}