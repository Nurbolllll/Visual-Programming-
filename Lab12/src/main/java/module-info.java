module kz.atu.lab12 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens kz.atu.lab12 to javafx.fxml;
    opens kz.atu.lab12.controller to javafx.fxml;
    exports kz.atu.lab12;
}
