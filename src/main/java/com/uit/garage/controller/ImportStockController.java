package com.uit.garage.controller;

import com.uit.garage.DBUtil;
import com.uit.garage.dao.StockDAO;
import com.uit.garage.model.Part;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public class ImportStockController {
    @FXML private ComboBox<Part> partCombo;
    @FXML private TextField quantityField;
    @FXML private DatePicker datePicker;
    @FXML private TextArea noteArea;
    @FXML private Button importBtn;

    private final StockDAO stockDAO = new StockDAO();

    @FXML
    public void initialize() {
        List<Part> parts = stockDAO.getAllParts();
        partCombo.setItems(FXCollections.observableArrayList(parts));
        datePicker.setValue(LocalDate.now());
    }

    @FXML
    public void handleImport() {
        Part part = partCombo.getValue();
        int qty;
        try {
            qty = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Số lượng không hợp lệ!").show();
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            boolean success = stockDAO.importStock(conn, part.getId(), datePicker.getValue(), qty, noteArea.getText());

            if (success) {
                conn.commit();
                new Alert(Alert.AlertType.INFORMATION, "Nhập kho thành công!").show();
                quantityField.clear();
                noteArea.clear();
            } else {
                conn.rollback();
                new Alert(Alert.AlertType.ERROR, "Nhập kho thất bại. Vui lòng thử lại.").show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Lỗi khi kết nối hoặc nhập kho.").show();
        }
    }
}
