module com.uit.garage {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.uit.garage to javafx.fxml;
    opens com.uit.garage.controller to javafx.fxml;

    exports com.uit.garage;
    exports com.uit.garage.controller;
    exports com.uit.garage.model;
    opens com.uit.garage.model to javafx.base, javafx.fxml;
}
