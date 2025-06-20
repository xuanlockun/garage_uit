package com.uit.garage.model;

import javafx.beans.property.*;

public class RevenueStatRow {
    private final IntegerProperty stt;
    private final StringProperty brand;
    private final IntegerProperty repairCount;
    private final DoubleProperty totalAmount;
    private final DoubleProperty rate;

    public RevenueStatRow(int stt, String brand, int repairCount, double totalAmount, double rate) {
        this.stt = new SimpleIntegerProperty(stt);
        this.brand = new SimpleStringProperty(brand);
        this.repairCount = new SimpleIntegerProperty(repairCount);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
        this.rate = new SimpleDoubleProperty(rate);
    }

    public int getStt() {
        return stt.get();
    }

    public IntegerProperty sttProperty() {
        return stt;
    }

    public String getBrand() {
        return brand.get();
    }

    public StringProperty brandProperty() {
        return brand;
    }

    public int getRepairCount() {
        return repairCount.get();
    }

    public IntegerProperty repairCountProperty() {
        return repairCount;
    }

    public double getTotalAmount() {
        return totalAmount.get();
    }

    public DoubleProperty totalAmountProperty() {
        return totalAmount;
    }

    public double getRate() {
        return rate.get();
    }

    public DoubleProperty rateProperty() {
        return rate;
    }
}
