package com.uit.garage.controller;

import com.uit.garage.dao.PartsDAO;
import com.uit.garage.dao.RepairDAO;
import com.uit.garage.model.Part;
import com.uit.garage.model.ReceiptRow;
import com.uit.garage.model.RepairDetail;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.text.DecimalFormat;
import java.time.LocalDate;

public class RepairController {

    @FXML private TextField licenseField;
    @FXML private DatePicker repairDatePicker;
    @FXML private TableView<RepairDetail> repairTable;
    @FXML private TableView<ReceiptRow> receiptTable;
    @FXML private TableColumn<ReceiptRow, Integer> colReceiptId;
    @FXML private TableColumn<ReceiptRow, String> colLicense;
    @FXML private TableColumn<ReceiptRow, String> colDate;
    private final DecimalFormat df = new DecimalFormat("#,###.##");
    private final ObservableList<ReceiptRow> receiptList = FXCollections.observableArrayList();

    @FXML private TableColumn<RepairDetail, String> colContent;
    @FXML private TableColumn<RepairDetail, Part> colPart;
    @FXML private TableColumn<RepairDetail, Integer> colQuantity;
    @FXML private TableColumn<RepairDetail, Double> colPrice;
    @FXML private TableColumn<RepairDetail, Double> colLaborCost;
    @FXML private TableColumn<RepairDetail, Double> colTotal;

    private final ObservableList<RepairDetail> repairData = FXCollections.observableArrayList();
    private final RepairDAO repairDAO = new RepairDAO();
    private final PartsDAO partsDAO = new PartsDAO();
    @FXML private TableColumn<ReceiptRow, String> colNote;
    @FXML private TableColumn<ReceiptRow, String> colStatus;

    @FXML
    public void initialize() {
        ObservableList<Part> partsList = FXCollections.observableArrayList(partsDAO.getAllParts());

        colContent.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
        colContent.setCellFactory(TextFieldTableCell.forTableColumn());
        colContent.setOnEditCommit(e -> e.getRowValue().setContent(e.getNewValue()));

        colPart.setCellValueFactory(cellData -> cellData.getValue().partProperty());
        colPart.setCellFactory(ComboBoxTableCell.forTableColumn(partsList));
        colPart.setOnEditCommit(e -> {
            RepairDetail detail = e.getRowValue();
            Part selectedPart = e.getNewValue();

            if (selectedPart != null) {
                detail.setPart(selectedPart);
                detail.setPrice(selectedPart.getPrice()); // 👈 cập nhật đơn giá theo part
                detail.recalculateTotal();
                repairTable.refresh(); // 👈 bắt buộc để TableView hiển thị lại
            }
        });


        colQuantity.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        colQuantity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colQuantity.setOnEditCommit(e -> {
            e.getRowValue().setQuantity(e.getNewValue());
            e.getRowValue().recalculateTotal();
        });

        colPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        colPrice.setCellFactory(TextFieldTableCell.forTableColumn(formattedDoubleConverter));
        colPrice.setOnEditCommit(e -> {
            e.getRowValue().setPrice(e.getNewValue());
            e.getRowValue().recalculateTotal();
        });

        colLaborCost.setCellValueFactory(cellData -> cellData.getValue().laborCostProperty().asObject());
        colLaborCost.setCellFactory(TextFieldTableCell.forTableColumn(formattedDoubleConverter));
        colLaborCost.setOnEditCommit(e -> {
            e.getRowValue().setLaborCost(e.getNewValue());
            e.getRowValue().recalculateTotal();
        });

        colTotal.setCellValueFactory(cellData -> cellData.getValue().totalProperty().asObject());
        colTotal.setCellFactory(column -> new TableCell<RepairDetail, Double>() {
            private final DecimalFormat formatter = new DecimalFormat("#,###.##");

            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(formatter.format(value));
                }
            }
        });
        repairTable.setItems(repairData);
        repairTable.setEditable(true);
        colReceiptId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        colLicense.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLicensePlate()));
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReceiptDate()));
        colNote.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNote()));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        receiptTable.setItems(receiptList);
        receiptTable.setOnMouseClicked(event -> {
            ReceiptRow selected = receiptTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                licenseField.setText(selected.getLicensePlate());
                repairDatePicker.setValue(LocalDate.parse(selected.getReceiptDate()));
                handleViewRepair();
            }
        });

        loadReceiptList();

    }

    @FXML
    private void handleAddRow() {
        Part defaultPart = partsDAO.getAllParts().isEmpty() ? null : partsDAO.getAllParts().get(0);
        double price = (defaultPart != null) ? defaultPart.getPrice() : 0.0;

        repairData.add(new RepairDetail("", defaultPart, 1, price, 0.0));
    }
    @FXML
    private void handleSaveRepair() {
        ReceiptRow selectedReceipt = receiptTable.getSelectionModel().getSelectedItem();
        if (selectedReceipt == null || repairData.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu dữ liệu", "Vui lòng chọn phiếu tiếp nhận và nhập nội dung sửa chữa.");
            return;
        }

        boolean success = repairDAO.insertRepairDetails(selectedReceipt.getId(), repairData);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã lưu phiếu sửa chữa.");
            handleReset();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không đủ hàng trong kho để xuất.");
        }
    }

    private void loadReceiptList() {
        receiptList.clear();
        for (var r : repairDAO.getAllReceipts()) {
            receiptList.add(new ReceiptRow(
                    r.getId(), r.getLicensePlate(), r.getReceiptDate(),
                    r.getNote(), r.getStatus()
            ));
        }
    }

    @FXML
    private void handleReset() {
        licenseField.clear();
        repairDatePicker.setValue(null);
        repairData.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    @FXML
    private void handleViewRepair() {
        String license = licenseField.getText();

        if (license.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu dữ liệu", "Vui lòng nhập biển số xe để tra cứu.");
            return;
        }

        ObservableList<RepairDetail> details = FXCollections.observableArrayList(repairDAO.getRepairsByLicense(license));
        if (details.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Không có dữ liệu", "Không tìm thấy sửa chữa cho xe này.");
        }

        repairData.setAll(details);
    }
    @FXML
    private void handleMarkDoneAndCreateInvoice() {
        ReceiptRow selectedReceipt = receiptTable.getSelectionModel().getSelectedItem();
        if (selectedReceipt == null) {
            showAlert(Alert.AlertType.WARNING, "Thiếu dữ liệu", "Vui lòng chọn phiếu tiếp nhận để tạo hóa đơn.");
            return;
        }

        boolean success = repairDAO.markReceiptDoneAndCreateInvoice(selectedReceipt.getId());
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã tạo hóa đơn cho phiếu tiếp nhận này.");
            loadReceiptList(); // Refresh lại danh sách
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tạo hóa đơn.");
        }
    }

    private final StringConverter<Double> formattedDoubleConverter = new StringConverter<>() {
        @Override
        public String toString(Double value) {
            if (value == null) return "";
            return df.format(value);
        }

        @Override
        public Double fromString(String string) {
            if (string == null || string.isEmpty()) return 0.0;
            try {
                // Xoá dấu phẩy khi parse
                return Double.parseDouble(string.replace(",", ""));
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
    };
}
