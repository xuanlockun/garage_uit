package com.uit.garage.model;

import javafx.beans.property.*;

public class StockReportRow {
    private final IntegerProperty stt;
    private final StringProperty partName;
    private final IntegerProperty beginQty;
    private final IntegerProperty inOutQty;
    private final IntegerProperty endQty;

    public StockReportRow(int stt, String partName, int beginQty, int inOutQty, int endQty) {
        this.stt = new SimpleIntegerProperty(stt);
        this.partName = new SimpleStringProperty(partName);
        this.beginQty = new SimpleIntegerProperty(beginQty);
        this.inOutQty = new SimpleIntegerProperty(inOutQty);
        this.endQty = new SimpleIntegerProperty(endQty);
    }

    public IntegerProperty sttProperty() { return stt; }
    public StringProperty partNameProperty() { return partName; }
    public IntegerProperty beginQtyProperty() { return beginQty; }
    public IntegerProperty inOutQtyProperty() { return inOutQty; }
    public IntegerProperty endQtyProperty() { return endQty; }
}
