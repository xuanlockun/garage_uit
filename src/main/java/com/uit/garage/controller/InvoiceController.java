package com.uit.garage.controller;

import com.uit.garage.dao.PaymentDAO;
import com.uit.garage.model.RepairDetail;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import com.uit.garage.dao.InvoiceDAO;
import com.uit.garage.model.InvoiceRow;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

public class InvoiceController {

    @FXML private TableView<InvoiceRow> invoiceTable;
    @FXML private TableColumn<InvoiceRow, Integer> colStt;
    @FXML private TableColumn<InvoiceRow, String> colLicense;
    @FXML private TableColumn<InvoiceRow, String> colBrand;
    @FXML private TableColumn<InvoiceRow, String> colCustomer;
    @FXML private TableColumn<InvoiceRow, Double> colTotal;
    @FXML private TableColumn<InvoiceRow, Void> colDetail;
    @FXML private TableColumn<InvoiceRow, Void> colPayment;
    private final DecimalFormat moneyFormat = new DecimalFormat("#,###.##");


    private final ObservableList<InvoiceRow> invoiceList = FXCollections.observableArrayList();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    @FXML
    public void initialize() {
        colStt.setCellValueFactory(new PropertyValueFactory<>("stt"));
        colLicense.setCellValueFactory(new PropertyValueFactory<>("licensePlate"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colTotal.setCellFactory(col -> new TableCell<InvoiceRow, Double>() {
            private final DecimalFormat moneyFormat = new DecimalFormat("#,###.##");

            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(moneyFormat.format(value));
                }
            }
        });

        colDetail.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Chi tiết");
            {
                btn.setOnAction(event -> {
                    InvoiceRow invoice = getTableView().getItems().get(getIndex());
                    showInvoiceDetail(invoice);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
        colPayment.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Thanh Toán");
            {
                btn.setOnAction(event -> {
                    InvoiceRow invoice = getTableView().getItems().get(getIndex());
                    showPaymentDialog(invoice);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        loadInvoices();
    }

    private void loadInvoices() {
        invoiceList.setAll(invoiceDAO.getAllInvoiceRows());
        invoiceTable.setItems(invoiceList);
    }

    private void showInvoiceDetail(InvoiceRow invoice) {
        List<RepairDetail> details = invoiceDAO.getRepairDetailsByInvoiceId(invoice.getStt());

        TableView<RepairDetail> detailTable = new TableView<>();
        TableColumn<RepairDetail, String> contentCol = new TableColumn<>("Nội dung");
        contentCol.setCellValueFactory(new PropertyValueFactory<>("content"));

        TableColumn<RepairDetail, String> partCol = new TableColumn<>("Phụ tùng");
        partCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPart().getName()));

        TableColumn<RepairDetail, Integer> quantityCol = new TableColumn<>("Số lượng");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<RepairDetail, Double> priceCol = new TableColumn<>("Đơn giá");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setCellFactory(col -> getFormattedMoneyCell());

        TableColumn<RepairDetail, Double> laborCol = new TableColumn<>("Tiền công");
        laborCol.setCellValueFactory(new PropertyValueFactory<>("laborCost"));
        laborCol.setCellFactory(col -> getFormattedMoneyCell());

        TableColumn<RepairDetail, Double> totalCol = new TableColumn<>("Thành tiền");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setCellFactory(col -> getFormattedMoneyCell());

        detailTable.getColumns().addAll(contentCol, partCol, quantityCol, priceCol, laborCol, totalCol);
        detailTable.setItems(FXCollections.observableArrayList(details));

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Chi tiết hóa đơn");
        VBox vbox = new VBox(10,
                new Label("Chủ xe: " + invoice.getCustomerName()),
                new Label("Biển số: " + invoice.getLicensePlate()),
                new Label("Hiệu xe: " + invoice.getBrand()),
                new Label("Tổng tiền: " + moneyFormat.format(invoice.getTotalAmount()) + " VND"),
                detailTable
        );
        vbox.setPrefSize(700, 400);
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void showPaymentDialog(InvoiceRow invoice) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Thanh Toán");

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(
                new Label("Chủ xe: " + invoice.getCustomerName()),
                new Label("Biển số: " + invoice.getLicensePlate()),
                new Label("Hiệu xe: " + invoice.getBrand()),
                new Label("Tổng tiền: " + invoice.getTotalAmount()),
                new Label("Nhập số tiền thu:")
        );

        TextField amountField = new TextField();
        vbox.getChildren().add(amountField);

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    double total = invoice.getTotalAmount();

                    if (amount > total) {
                        Alert error = new Alert(Alert.AlertType.WARNING, "Số tiền thu không được vượt quá tổng tiền (" + total + " VND)!");
                        error.showAndWait();
                        return null;
                    }

                    LocalDate now = LocalDate.now();
                    boolean success = PaymentDAO.insertPayment(invoice.getStt(), now, amount);
                    if (success) {
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Đã thu tiền thành công!");
                        successAlert.showAndWait();
                        loadInvoices();
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR, "Lưu thanh toán thất bại!");
                        error.showAndWait();
                    }
                } catch (NumberFormatException e) {
                    Alert error = new Alert(Alert.AlertType.ERROR, "Số tiền không hợp lệ!");
                    error.showAndWait();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }


    // TableCell dùng cho các cột hiển thị tiền
    private TableCell<RepairDetail, Double> getFormattedMoneyCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(moneyFormat.format(value));
                }
            }
        };
    }

}
