package com.uit.garage;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.ResultSet;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseHelper.createTable();

        TextField nameInput = new TextField();
        nameInput.setPromptText("Enter name");

        Button addButton = new Button("Add");
        Button showButton = new Button("Show All");

        TextArea output = new TextArea();
        output.setEditable(false);

        addButton.setOnAction(e -> {
            String name = nameInput.getText();
            if (!name.isEmpty()) {
                DatabaseHelper.insert(name);
                nameInput.clear();
            }
        });

        showButton.setOnAction(e -> {
            ResultSet rs = DatabaseHelper.readAll();
            StringBuilder sb = new StringBuilder();
            try {
                while (rs.next()) {
                    sb.append(rs.getInt("id"))
                            .append(": ")
                            .append(rs.getString("name"))
                            .append("\n");
                }
                rs.getStatement().getConnection().close(); // clean up
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            output.setText(sb.toString());
        });

        VBox root = new VBox(10, nameInput, addButton, showButton, output);
        root.setStyle("-fx-padding: 20");
        primaryStage.setScene(new Scene(root, 300, 400));
        primaryStage.setTitle("JavaFX + SQLite");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
