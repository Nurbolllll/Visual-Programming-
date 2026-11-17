package kz.atu.lab12.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import kz.atu.lab12.model.Book;
import kz.atu.lab12.repository.BookRepository;

import java.sql.SQLException;
import java.util.Optional;

public class BookController {
    @FXML private TableView<Book> tableBooks;
    @FXML private TableColumn<Book, Number> colId;
    @FXML private TableColumn<Book, String> colTitle, colAuthor;
    @FXML private TableColumn<Book, Number> colYear;
    @FXML private TextField txtTitle, txtAuthor, txtYear, txtSearch;
    @FXML private Label lblStatus;

    private final BookRepository repository = new BookRepository();
    private final ObservableList<Book> items = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().id()));
        colTitle.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().title()));
        colAuthor.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().author()));
        colYear.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().year()));

        // Живой поиск сразу по названию И по автору
        FilteredList<Book> filtered = new FilteredList<>(items, b -> true);
        tableBooks.setItems(filtered);
        txtSearch.textProperty().addListener((obs, oldV, newV) -> {
            String q = newV.trim().toLowerCase();
            filtered.setPredicate(b ->
                    b.title().toLowerCase().contains(q) ||
                            b.author().toLowerCase().contains(q)
            );
        });

        tableBooks.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtTitle.setText(newSel.title());
                txtAuthor.setText(newSel.author());
                txtYear.setText(String.valueOf(newSel.year()));
            }
        });

        try {
            repository.initializeDatabase();
            refresh();
        } catch (SQLException e) {
            showError("Не удалось открыть базу данных");
        }
    }

    private void refresh() throws SQLException {
        items.setAll(repository.findAll());
        lblStatus.setText("Книг в каталоге: " + items.size());
    }

    private int readYear() {
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        if (title.isBlank() || author.isBlank()) {
            throw new IllegalArgumentException("Заполните название и автора книги");
        }
        int year;
        try {
            year = Integer.parseInt(txtYear.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Год издания должен быть целым числом");
        }
        if (year < 1450 || year > 2026) {
            throw new IllegalArgumentException("Год издания должен быть от 1450 до 2026");
        }
        return year;
    }

    @FXML
    private void onAdd() {
        try {
            int year = readYear();
            repository.insert(txtTitle.getText().trim(), txtAuthor.getText().trim(), year);
            clearFields();
            refresh();
        } catch (IllegalArgumentException | SQLException e) {
            showError(e instanceof SQLException ? "Ошибка сохранения в БД" : e.getMessage());
        }
    }

    @FXML
    private void onUpdate() {
        Book selected = tableBooks.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите книгу в таблице для изменения");
            return;
        }
        try {
            int year = readYear();
            repository.update(selected.id(), txtTitle.getText().trim(), txtAuthor.getText().trim(), year);
            refresh();
        } catch (IllegalArgumentException | SQLException e) {
            showError(e instanceof SQLException ? "Ошибка обновления в БД" : e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        Book selected = tableBooks.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите книгу для удаления");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Удалить книгу «" + selected.title() + "»?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                repository.delete(selected.id());
                clearFields();
                refresh();
            } catch (SQLException e) {
                showError("Ошибка удаления из БД");
            }
        }
    }

    @FXML
    private void onClear() {
        clearFields();
        tableBooks.getSelectionModel().clearSelection();
    }

    @FXML
    private void onRefresh() {
        try {
            refresh();
        } catch (SQLException e) {
            showError("Ошибка обновления данных");
        }
    }

    private void clearFields() {
        txtTitle.clear();
        txtAuthor.clear();
        txtYear.clear();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}
