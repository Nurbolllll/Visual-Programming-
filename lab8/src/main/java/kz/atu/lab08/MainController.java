package kz.atu.lab08;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

public class MainController {

    @FXML private TextArea txtNotes;
    @FXML private Label lblStatus;

    private Color currentColor = Color.BLACK;

    // Переменные для отслеживания состояния файла
    private File currentFile = null;
    private boolean isTextChanged = false;

    @FXML
    public void initialize() {
        // Слушатель изменений текста, чтобы знать, менял ли что-то пользователь
        txtNotes.textProperty().addListener((observable, oldValue, newValue) -> {
            isTextChanged = true;
        });
    }

    @FXML
    private void onOpen() {
        if (!confirmDiscardChanges()) return;

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Открыть заметку");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"));
        File file = chooser.showOpenDialog(txtNotes.getScene().getWindow());
        if (file == null) return;
        try {
            txtNotes.setText(Files.readString(file.toPath(), StandardCharsets.UTF_8));
            currentFile = file;
            isTextChanged = false; // Сбрасываем флаг, так как файл только что открыт
            lblStatus.setText("Открыт: " + file.getName());
        } catch (IOException ex) {
            showError(ex.getMessage());
        }
    }

    // Кнопка простого "Сохранить" (Самостоятельное задание №2)
    @FXML
    private void onSave() {
        if (currentFile == null) {
            onSaveAs(); // Если файл новый, перенаправляем на "Сохранить как..."
        } else {
            saveToFile(currentFile);
        }
    }

    @FXML
    private void onSaveAs() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Сохранить заметку как...");
        chooser.setInitialFileName(currentFile != null ? currentFile.getName() : "note.txt");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"));
        File file = chooser.showSaveDialog(txtNotes.getScene().getWindow());
        if (file == null) return;

        saveToFile(file);
    }

    private void saveToFile(File file) {
        try {
            Files.writeString(file.toPath(), txtNotes.getText(), StandardCharsets.UTF_8);
            currentFile = file;
            isTextChanged = false; // Текст сохранен
            lblStatus.setText("Сохранён: " + file.getName());
        } catch (IOException ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    private void onChooseColor() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("color-dialog.fxml"));
        Parent root = loader.load();
        ColorDialogController controller = loader.getController();
        controller.setInitialColor(currentColor);

        Stage dialog = new Stage();
        dialog.initOwner(txtNotes.getScene().getWindow());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Цвет текста");
        dialog.setScene(new Scene(root));
        dialog.showAndWait();

        controller.getSelectedColor().ifPresent(color -> {
            currentColor = color;
            txtNotes.setStyle("-fx-text-fill: " + toCss(color) + ";");
            lblStatus.setText("Цвет текста изменён");
        });
    }

    @FXML
    private void onExit() {
        if (confirmDiscardChanges()) {
            System.exit(0);
        }
    }

    @FXML
    private void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("О программе");
        alert.setHeaderText("Редактор заметок v1.1");
        alert.setContentText("Лабораторная работа №8\nРазработчик: Студент 3 курса");
        alert.showAndWait();
    }

    // Метод проверки несохраненных изменений (Самостоятельное задание №1)
    private boolean confirmDiscardChanges() {
        if (isTextChanged) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Предупреждение");
            alert.setHeaderText("Изменения не сохранены");
            alert.setContentText("Вы действительно хотите продолжить без сохранения изменений?");

            Optional<ButtonType> result = alert.showAndWait();
            return result.isPresent() && result.get() == ButtonType.OK;
        }
        return true;
    }

    private String toCss(Color c) {
        return String.format("#%02X%02X%02X",
                Math.round(c.getRed() * 255),
                Math.round(c.getGreen() * 255),
                Math.round(c.getBlue() * 255));
    }

    private void showError(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Ошибка ввода-вывода");
        a.setHeaderText("Не удалось выполнить операцию");
        a.setContentText(message);
        a.showAndWait();
    }
}
