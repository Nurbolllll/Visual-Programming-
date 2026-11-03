package com.example.lab1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Загрузка FXML-файла интерфейса
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/lab1/hello-view.fxml"));
        // Размеры окна (можно настроить под себя)
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);

        // Установка заголовка окна по заданию №1
        stage.setTitle("Визитная карточка");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] presidential) {
        launch();
    }
}
