package com.example.lab9;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        // Увеличиваем размер окна, чтобы таблица была полностью видна
        Scene scene = new Scene(fxmlLoader.load(), 650, 550);
        stage.setTitle("Бронирование гостиницы — Вариант 5");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}