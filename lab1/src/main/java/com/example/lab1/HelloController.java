package com.example.lab1;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {

    @FXML
    private Label welcomeText; // если этот элемент остался в fxml

    @FXML
    protected void onHelloButtonClick() {
        System.out.println("Кнопка нажата!");
    }
}
