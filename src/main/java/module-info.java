module com.uit.garage {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.uit.garage to javafx.fxml;
    exports com.uit.garage;
}