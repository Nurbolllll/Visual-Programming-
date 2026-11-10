module com.example.lab11 {
    requires javafx.controls;
    requires javafx.fxml;


    opens kz.atu.lab11 to javafx.fxml;
    exports kz.atu.lab11;
    exports kz.atu.lab11.controller;
    opens kz.atu.lab11.controller to javafx.fxml;
}