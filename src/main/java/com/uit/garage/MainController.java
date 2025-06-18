package com.uit.garage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MainController {

    @FXML
    private AnchorPane contentPane;

    public void ShowReceipt() {
        loadView("/com/uit/garage/ReceiptView.fxml");
    }

    public void ShowRepair() {
        loadView("/com/uit/garage/RepairView.fxml");
    }
    public void ShowCustomer() {
        loadView("/com/uit/garage/CustomerView.fxml");
    }
    public void ShowInvoice() {
        loadView("/com/uit/garage/InvoiceView.fxml");
    }
    private void loadView(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
