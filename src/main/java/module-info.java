module com.uit.garage {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.uit.garage to javafx.fxml;
    exports com.uit.garage;
}