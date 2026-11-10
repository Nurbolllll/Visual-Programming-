package kz.atu.lab11.controller;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import kz.atu.lab11.model.Book;
import kz.atu.lab11.service.BookService;

import java.util.Optional;

public class BookController {
    @FXML private TableView<Book> tableBooks;
    @FXML private TableColumn<Book, String> colTitle, colAuthor;
    @FXML private TableColumn<Book, Number> colRating;
    @FXML private TextField txtTitle, txtAuthor, txtRating, txtSearch;
    @FXML private ComboBox<String> cmbAuthorFilter;
    @FXML private Label lblCount, lblAvgRating;

    private final BookService service = new BookService();
    private FilteredList<Book> filtered;

    @FXML
    private void initialize() {
        colTitle.setCellValueFactory(c -> c.getValue().titleProperty());
        colAuthor.setCellValueFactory(c -> c.getValue().authorProperty());
        colRating.setCellValueFactory(c -> c.getValue().ratingProperty());

        // Тестовые данные
        service.addBook("Война и мир", "Лев Толстой", 4.8);
        service.addBook("Преступление и наказание", "Фёдор Достоевский", 4.9);
        service.addBook("Мастер и Маргарита", "Михаил Булгаков", 5.0);

        filtered = new FilteredList<>(service.getBooks(), s -> true);
        SortedList<Book> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tableBooks.comparatorProperty());
        tableBooks.setItems(sorted);

        updateAuthorFilterCombo();
        cmbAuthorFilter.setValue("Все");

        txtSearch.textProperty().addListener((o, a, b) -> applyFilter());
        cmbAuthorFilter.valueProperty().addListener((o, a, b) -> applyFilter());

        // Заполнение полей при выборе строки в таблице
        tableBooks.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtTitle.setText(newVal.getTitle());
                txtAuthor.setText(newVal.getAuthor());
                txtRating.setText(String.valueOf(newVal.getRating()));
            }
        });

        applyFilter();
    }

    private double readRating() {
        try {
            return Double.parseDouble(txtRating.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Введите числовой формат рейтинга");
        }
    }

    @FXML
    private void onAdd() {
        try {
            service.addBook(txtTitle.getText(), txtAuthor.getText(), readRating());
            updateAuthorFilterCombo();
            applyFilter();
            clearFields();
            // Небольшая подсказка пользователю
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }

    }

    @FXML
    private void onUpdate() {
        try {
            Book selected = tableBooks.getSelectionModel().getSelectedItem();
            service.updateBook(selected, txtTitle.getText(), txtAuthor.getText(), readRating());
            updateAuthorFilterCombo();
            applyFilter();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        try {
            Book selected = tableBooks.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new IllegalArgumentException("Выберите книгу для удаления");
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Подтверждение удаления");
            confirm.setHeaderText(null);
            confirm.setContentText("Удалить книгу \"" + selected.getTitle() + "\"?");
            Optional<ButtonType> result = confirm.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                service.deleteBook(selected);
                updateAuthorFilterCombo();
                applyFilter();
                clearFields();
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void applyFilter() {
        String q = txtSearch.getText().trim().toLowerCase();
        String author = cmbAuthorFilter.getValue();

        filtered.setPredicate(b -> {
            boolean matchesSearch = b.getTitle().toLowerCase().contains(q) ||
                    b.getAuthor().toLowerCase().contains(q);
            boolean matchesAuthor = (author == null || author.equals("Все") || b.getAuthor().equals(author));
            return matchesSearch && matchesAuthor;
        });

        lblCount.setText("Найдено книг: " + filtered.size());
        lblAvgRating.setText(String.format("Средний рейтинг: %.2f", service.getAverageRating()));
    }

    private void updateAuthorFilterCombo() {
        String currentVal = cmbAuthorFilter.getValue();
        cmbAuthorFilter.setItems(service.getUniqueAuthors());
        if (cmbAuthorFilter.getItems().contains(currentVal)) {
            cmbAuthorFilter.setValue(currentVal);
        } else {
            cmbAuthorFilter.setValue("Все");
        }
    }

    private void clearFields() {
        txtTitle.clear();
        txtAuthor.clear();
        txtRating.clear();
        tableBooks.getSelectionModel().clearSelection();
    }

    private void showError(String m) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Ошибка");
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
    @FXML
    private void onClear() {
        clearFields();
    }

}