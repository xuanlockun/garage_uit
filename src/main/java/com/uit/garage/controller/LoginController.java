package com.uit.garage.controller;

import com.uit.garage.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:garage.db")) {
            String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user);
            stmt.setString(2, pass);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

                MainApp.currentRole = role;
                MainApp.currentUsername = user;

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uit/garage/MainLayout.fxml"));
                Scene mainScene = new Scene(loader.load());

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(mainScene);
                stage.setTitle("Garage Management - Role: " + role);
            } else {
                errorLabel.setText("Sai tài khoản hoặc mật khẩu.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Lỗi kết nối cơ sở dữ liệu.");
        }
    }
}
