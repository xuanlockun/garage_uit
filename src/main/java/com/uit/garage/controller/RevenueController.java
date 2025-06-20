package com.uit.garage.controller;

import com.uit.garage.dao.RevenueDAO;
import com.uit.garage.model.RevenueStatRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

public class RevenueController {

    @FXML private ComboBox<Integer> monthBox;
    @FXML private ComboBox<Integer> yearBox;
    @FXML private Label totalRevenueLabel;

    @FXML private TableView<RevenueStatRow> revenueTable;
    @FXML private TableColumn<RevenueStatRow, Integer> colStt;
    @FXML private TableColumn<RevenueStatRow, String> colBrand;
    @FXML private TableColumn<RevenueStatRow, Integer> colCount;
    @FXML private TableColumn<RevenueStatRow, Double> colAmount;
    @FXML private TableColumn<RevenueStatRow, Double> colRate;

    private final RevenueDAO revenueDAO = new RevenueDAO();
    private final ObservableList<RevenueStatRow> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colStt.setCellValueFactory(cell -> cell.getValue().sttProperty().asObject());
        colBrand.setCellValueFactory(cell -> cell.getValue().brandProperty());
        colCount.setCellValueFactory(cell -> cell.getValue().repairCountProperty().asObject());
        colAmount.setCellValueFactory(cell -> cell.getValue().totalAmountProperty().asObject());
        colRate.setCellValueFactory(cell -> cell.getValue().rateProperty().asObject());

        revenueTable.setItems(data);

        // Khởi tạo combobox tháng và năm
        monthBox.setItems(FXCollections.observableArrayList(IntStream.rangeClosed(1, 12).boxed().toList()));
        int currentYear = LocalDate.now().getYear();
        yearBox.setItems(FXCollections.observableArrayList(IntStream.rangeClosed(currentYear - 5, currentYear + 1).boxed().toList()));

        monthBox.setValue(LocalDate.now().getMonthValue());
        yearBox.setValue(currentYear);
    }

    @FXML
    public void handleStatistics() {
        Integer month = monthBox.getValue();
        Integer year = yearBox.getValue();

        if (month == null || year == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn tháng và năm.");
            alert.showAndWait();
            return;
        }

        List<RevenueStatRow> stats = revenueDAO.getRevenueStats(month, year);
        data.setAll(stats);

        double total = stats.stream().mapToDouble(RevenueStatRow::getTotalAmount).sum();
        totalRevenueLabel.setText("Tổng doanh thu: " + String.format("%,.0f", total) + " VND");
    }
}
