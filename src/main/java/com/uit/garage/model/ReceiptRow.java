package com.uit.garage.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ReceiptRow {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty licensePlate;
    private final SimpleStringProperty receiptDate;

    public ReceiptRow(int id, String licensePlate, String receiptDate) {
        this.id = new SimpleIntegerProperty(id);
        this.licensePlate = new SimpleStringProperty(licensePlate);
        this.receiptDate = new SimpleStringProperty(receiptDate);
    }

    public int getId() { return id.get(); }
    public String getLicensePlate() { return licensePlate.get(); }
    public String getReceiptDate() { return receiptDate.get(); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty licensePlateProperty() { return licensePlate; }
    public StringProperty receiptDateProperty() { return receiptDate; }
}
