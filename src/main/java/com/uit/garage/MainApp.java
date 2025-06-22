package com.uit.garage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    public static String currentRole = "";
    public static String currentUsername = "";

    @Override
    public void start(Stage primaryStage    ) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uit/garage/Login.fxml"));

        primaryStage.setTitle("Garage UIT");
        primaryStage.setScene(new Scene(loader.load(), 600, 400));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.uit.garage.DBUtil");
            System.out.println("DBUtil loaded.");
        } catch (ClassNotFoundException e) {
            System.out.println("Không tìm thấy DBUtil.");
            e.printStackTrace();
        }
        launch(args);
    }
}
