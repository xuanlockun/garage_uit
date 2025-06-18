package com.uit.garage.controller;

import com.uit.garage.dao.CustomerDAO;
import com.uit.garage.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CustomerController {

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> colId;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colAddress;
    @FXML private TableColumn<Customer, String> colPhone;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

    private Customer selectedCustomer = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        colAddress.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAddress()));
        colPhone.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));

        customerTable.setItems(customerList);
        customerTable.setOnMouseClicked(event -> {
            selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
            if (selectedCustomer != null) {
                nameField.setText(selectedCustomer.getName());
                addressField.setText(selectedCustomer.getAddress());
                phoneField.setText(selectedCustomer.getPhone());
            }
        });

        loadCustomers();
    }

    private void loadCustomers() {
        customerList.setAll(customerDAO.getAllCustomers());
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText();
        String address = addressField.getText();
        String phone = phoneField.getText();

        if (name.isEmpty()) {
            showAlert("Tên không được để trống");
            return;
        }

        Customer customer = new Customer(name, address, phone);
        customerDAO.insertCustomer(customer);
        loadCustomers();
        handleClear();
    }

    @FXML
    private void handleUpdate() {
        if (selectedCustomer == null) {
            showAlert("Hãy chọn khách hàng để sửa.");
            return;
        }

        selectedCustomer.setName(nameField.getText());
        selectedCustomer.setAddress(addressField.getText());
        selectedCustomer.setPhone(phoneField.getText());

        customerDAO.updateCustomer(selectedCustomer);
        loadCustomers();
        handleClear();
    }

    @FXML
    private void handleDelete() {
        if (selectedCustomer == null) {
            showAlert("Hãy chọn khách hàng để xoá.");
            return;
        }

        customerDAO.deleteCustomer(selectedCustomer.getId());
        loadCustomers();
        handleClear();
    }

    @FXML
    private void handleClear() {
        nameField.clear();
        addressField.clear();
        phoneField.clear();
        selectedCustomer = null;
        customerTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
