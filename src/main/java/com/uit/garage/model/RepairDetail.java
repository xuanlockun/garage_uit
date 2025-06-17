package com.uit.garage.model;

import javafx.beans.property.*;

public class RepairDetail {
    private Integer id; // id của dòng repair_detail
    private Integer receiptId;
    private final StringProperty content;
    private final ObjectProperty<Part> part;
    private final IntegerProperty quantity;
    private final DoubleProperty price;
    private final DoubleProperty laborCost;
    private final DoubleProperty total;

    public RepairDetail(Integer id, Integer receiptId, String content, Part part, int quantity, double price, double laborCost) {
        this.id = id;
        this.receiptId = receiptId;
        this.content = new SimpleStringProperty(content);
        this.part = new SimpleObjectProperty<>(part);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
        this.laborCost = new SimpleDoubleProperty(laborCost);
        this.total = new SimpleDoubleProperty(price * quantity + laborCost);
    }
    public RepairDetail(String content, Part part, int quantity, double price, double laborCost) {
        this(null, null, content, part, quantity, price, laborCost);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getReceiptId() { return receiptId; }
    public void setReceiptId(Integer receiptId) { this.receiptId = receiptId; }
    // Content
    public StringProperty contentProperty() {
        return content;
    }

    public String getContent() {
        return content.get();
    }

    public void setContent(String value) {
        content.set(value);
    }

    // Part
    public ObjectProperty<Part> partProperty() {
        return part;
    }

    public Part getPart() {
        return part.get();
    }

    public void setPart(Part value) {
        part.set(value);
    }

    // Quantity
    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int value) {
        quantity.set(value);
        recalculateTotal();
    }

    // Price
    public DoubleProperty priceProperty() {
        return price;
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double value) {
        price.set(value);
        recalculateTotal();
    }

    // LaborCost
    public DoubleProperty laborCostProperty() {
        return laborCost;
    }

    public double getLaborCost() {
        return laborCost.get();
    }

    public void setLaborCost(double value) {
        laborCost.set(value);
        recalculateTotal();
    }

    // Total
    public DoubleProperty totalProperty() {
        return total;
    }

    public double getTotal() {
        return total.get();
    }

    public void recalculateTotal() {
        total.set(quantity.get() * price.get() + laborCost.get());
    }
}
