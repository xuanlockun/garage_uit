package com.uit.garage.controller;

import com.uit.garage.model.Customer;
import com.uit.garage.model.Vehicle;
import com.uit.garage.model.Receipt;
import com.uit.garage.dao.ReceiptDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class ReceiptController {

    @FXML private TextField ownerNameField;
    @FXML private TextField licensePlateField;
    @FXML private TextField brandField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private DatePicker receiptDatePicker;
    @FXML private TextField searchField;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> addressColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TextArea noteArea;




    private final ReceiptDAO receiptDAO = new ReceiptDAO();

    @FXML
    private void handleSave() {
        try {
            String name = ownerNameField.getText();
            String license = licensePlateField.getText();
            String brand = brandField.getText();
            String address = addressField.getText();
            String phone = phoneField.getText();
            LocalDate date = receiptDatePicker.getValue();

            if (name.isEmpty() || license.isEmpty() || brand.isEmpty() || date == null) {
                showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng điền đầy đủ thông tin cần thiết.");
                return;
            }

            Customer customer = new Customer(name, address, phone);
            int customerId = receiptDAO.insertCustomer(customer);

            Vehicle vehicle = new Vehicle(license, brand, customerId);
            int vehicleId = receiptDAO.insertVehicle(vehicle);
            String note = noteArea.getText();
            Receipt receipt = new Receipt(vehicleId, date, note);
            receiptDAO.insertReceipt(receipt);

            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã lưu thông tin tiếp nhận xe.");
            handleReset();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu dữ liệu.");
        }
    }

    @FXML
    private void handleReset() {
        ownerNameField.clear();
        licensePlateField.clear();
        brandField.clear();
        addressField.clear();
        phoneField.clear();
        noteArea.clear();
        receiptDatePicker.setValue(null);
    }
    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        addressColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAddress()));
        phoneColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));

        customerTable.setOnMouseClicked(event -> {
            Customer selected = customerTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                ownerNameField.setText(selected.getName());
                addressField.setText(selected.getAddress());
                phoneField.setText(selected.getPhone());
            }
        });
    }
    @FXML
    private void handleSearchCustomer() {
        String keyword = searchField.getText().trim();
        if (!keyword.isEmpty()) {
            List<Customer> foundCustomers = receiptDAO.searchCustomerByName(keyword);
            customerTable.getItems().setAll(foundCustomers);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
