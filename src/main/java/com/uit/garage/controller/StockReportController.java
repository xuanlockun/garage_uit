package com.uit.garage.controller;

import com.uit.garage.dao.StockDAO;
import com.uit.garage.model.StockReportRow;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.IntStream;

public class StockReportController {

    @FXML private ComboBox<Integer> monthComboBox;
    @FXML private TableView<StockReportRow> stockTable;
    @FXML private TableColumn<StockReportRow, Integer> colStt;
    @FXML private TableColumn<StockReportRow, String> colPartName;
    @FXML private TableColumn<StockReportRow, Integer> colBeginQty;
    @FXML private TableColumn<StockReportRow, Integer> colInOutQty;
    @FXML private TableColumn<StockReportRow, Integer> colEndQty;

    @FXML
    public void initialize() {
        monthComboBox.setItems(FXCollections.observableArrayList(IntStream.rangeClosed(1, 12).boxed().toList()));
        monthComboBox.setValue(YearMonth.now().getMonthValue());

        colStt.setCellValueFactory(data -> data.getValue().sttProperty().asObject());
        colPartName.setCellValueFactory(data -> data.getValue().partNameProperty());
        colBeginQty.setCellValueFactory(data -> data.getValue().beginQtyProperty().asObject());
        colInOutQty.setCellValueFactory(data -> data.getValue().inOutQtyProperty().asObject());
        colEndQty.setCellValueFactory(data -> data.getValue().endQtyProperty().asObject());
    }

    @FXML
    private void handleViewReport() {
        int month = monthComboBox.getValue();
        int year = YearMonth.now().getYear(); // hoặc cho phép chọn năm riêng

        List<StockReportRow> reportRows = StockDAO.getStockReport(month, year);
        stockTable.setItems(FXCollections.observableArrayList(reportRows));
    }
}
