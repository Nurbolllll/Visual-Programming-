package kz.atu.lab11.service;

import javafx.collections.*;
import kz.atu.lab11.model.Book;

import java.util.List;
import java.util.stream.Collectors;

public class BookService {
    private final ObservableList<Book> books = FXCollections.observableArrayList();

    public ObservableList<Book> getBooks() {
        return books;
    }

    public void addBook(String title, String author, double rating) {
        validate(title, author, rating);
        checkDuplicate(title, author, null);
        books.add(new Book(title.trim(), author.trim(), rating));
    }

    public void updateBook(Book book, String title, String author, double rating) {
        if (book == null) throw new IllegalArgumentException("Книга не выбрана");
        validate(title, author, rating);
        checkDuplicate(title, author, book);
        book.setTitle(title.trim());
        book.setAuthor(author.trim());
        book.setRating(rating);
    }

    public void deleteBook(Book book) {
        if (book == null) throw new IllegalArgumentException("Книга не выбрана");
        books.remove(book);
    }

    private void validate(String title, String author, double rating) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Введите название книги");
        if (author == null || author.isBlank()) throw new IllegalArgumentException("Введите автора книги");
        if (!Double.isFinite(rating) || rating < 0 || rating > 5)
            throw new IllegalArgumentException("Рейтинг должен быть в диапазоне от 0 до 5");
    }

    private void checkDuplicate(String title, String author, Book currentBook) {
        boolean exists = books.stream()
                .anyMatch(b -> b != currentBook &&
                        b.getTitle().equalsIgnoreCase(title.trim()) &&
                        b.getAuthor().equalsIgnoreCase(author.trim()));
        if (exists) {
            throw new IllegalArgumentException("Книга с таким названием и автором уже существует в каталоге");
        }
    }

    public ObservableList<String> getUniqueAuthors() {
        List<String> authors = books.stream()
                .map(Book::getAuthor)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        ObservableList<String> list = FXCollections.observableArrayList("Все");
        list.addAll(authors);
        return list;
    }

    public double getAverageRating() {
        if (books.isEmpty()) return 0.0;
        return books.stream().mapToDouble(Book::getRating).average().orElse(0.0);
    }
}