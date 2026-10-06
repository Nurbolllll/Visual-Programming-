package com.example.lab6;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class HelloController {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbFilterGenre;
    @FXML private ListView<String> listGenres;

    @FXML private TableView<Book> tableBooks;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, String> colGenre;
    @FXML private TableColumn<Book, Integer> colYear;

    @FXML private TextField txtTitle;
    @FXML private TextField txtAuthor;
    @FXML private ComboBox<String> cmbGenre;
    @FXML private TextField txtYear;
    @FXML private Label lblCount;

    private final ObservableList<Book> books = FXCollections.observableArrayList();
    private FilteredList<Book> filteredBooks;

    @FXML
    public void initialize() {
        // 1. Инициализация колонок таблицы
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));

        // 2. Начальные данные
        books.addAll(
                new Book("Война и мир", "Лев Толстой", "Роман", 1869),
                new Book("Преступление и наказание", "Фёдор Достоевский", "Роман", 1866),
                new Book("Мастер и Маргарита", "Михаил Булгаков", "Фантастика", 1967),
                new Book("1984", "Джордж Оруэлл", "Антиутопия", 1949),
                new Book("Шерлок Холмс", "Артур Конан Дойл", "Детектив", 1892)
        );

        // 3. Настройка фильтруемого списка
        filteredBooks = new FilteredList<>(books, b -> true);
        tableBooks.setItems(filteredBooks);

        // 4. Заполнение списков жанров[cite: 3, 6]
        cmbGenre.getItems().addAll("Роман", "Фантастика", "Антиутопия", "Детектив", "Поэзия");
        cmbFilterGenre.getItems().addAll("Все", "Роман", "Фантастика", "Антиутопия", "Детектив", "Поэзия");
        cmbFilterGenre.setValue("Все");

        listGenres.getItems().addAll("Все книги", "Роман", "Фантастика", "Антиутопия", "Детектив", "Поэзия");

        // 5. Слушатели (Listeners)
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        cmbFilterGenre.setOnAction(e -> applyFilter());

        listGenres.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cmbFilterGenre.setValue(newVal.equals("Все книги") ? "Все" : newVal);
                applyFilter();
            }
        });

        // Самостоятельное задание: перенос данных выбранной строки в форму ввода
        tableBooks.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selectedBook) -> {
            if (selectedBook != null) {
                txtTitle.setText(selectedBook.getTitle());
                txtAuthor.setText(selectedBook.getAuthor());
                cmbGenre.setValue(selectedBook.getGenre());
                txtYear.setText(String.valueOf(selectedBook.getYear()));
            }
        });

        updateCount();
    }

    @FXML
    private void onAddClick() {
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String genre = cmbGenre.getValue();
        String yearText = txtYear.getText().trim();

        if (title.isBlank() || author.isBlank() || genre == null || yearText.isBlank()) {
            showError("Заполните все поля.");
            return;
        }

        try {
            int year = Integer.parseInt(yearText);
            if (year <= 0 || year > 2026) {
                showError("Укажите корректный год.");
                return;
            }

            books.add(new Book(title, author, genre, year));
            clearInput();
            applyFilter();
        } catch (NumberFormatException e) {
            showError("Год должен быть целым числом.");
        }
    }

    @FXML
    private void onDeleteClick() {
        Book selected = tableBooks.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите книгу для удаления.");
            return;
        }

        // Подтверждение удаления через Alert[cite: 6]
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Удалить выбранную книгу?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                books.remove(selected);
                clearInput();
                applyFilter();
            }
        });
    }

    @FXML
    private void onEditClick() {
        Book selected = tableBooks.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите книгу для редактирования.");
            return;
        }

        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String genre = cmbGenre.getValue();
        String yearText = txtYear.getText().trim();

        if (title.isBlank() || author.isBlank() || genre == null || yearText.isBlank()) {
            showError("Заполните все поля.");
            return;
        }

        try {
            int year = Integer.parseInt(yearText);
            selected.setTitle(title);
            selected.setAuthor(author);
            selected.setGenre(genre);
            selected.setYear(year);

            tableBooks.refresh();
            applyFilter();
        } catch (NumberFormatException e) {
            showError("Год должен быть целым числом.");
        }
    }

    @FXML
    private void onClearFilterClick() {
        txtSearch.clear();
        cmbFilterGenre.setValue("Все");
        listGenres.getSelectionModel().clearSelection();
        applyFilter();
    }

    private void applyFilter() {
        String search = txtSearch.getText().trim().toLowerCase();
        String genre = cmbFilterGenre.getValue();

        filteredBooks.setPredicate(book -> {
            // Поиск одновременно по названию и автору[cite: 5, 6]
            boolean matchesSearch = book.getTitle().toLowerCase().contains(search) ||
                    book.getAuthor().toLowerCase().contains(search);

            boolean matchesGenre = genre == null ||
                    genre.equals("Все") ||
                    book.getGenre().equals(genre);

            return matchesSearch && matchesGenre;
        });

        updateCount();
    }

    private void updateCount() {
        lblCount.setText("Найдено записей: " + tableBooks.getItems().size());
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearInput() {
        txtTitle.clear();
        txtAuthor.clear();
        txtYear.clear();
        cmbGenre.getSelectionModel().clearSelection();
        tableBooks.getSelectionModel().clearSelection();
        txtTitle.requestFocus();
    }
}