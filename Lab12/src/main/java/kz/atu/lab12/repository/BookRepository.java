package kz.atu.lab12.repository;

import kz.atu.lab12.model.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {
    private static final String URL = "jdbc:sqlite:books.db";

    public void initializeDatabase() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS books (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                year INTEGER NOT NULL CHECK (year BETWEEN 1450 AND 2026)
            )
            """;
        boolean isEmpty = false;
        try (Connection c = DriverManager.getConnection(URL);
             Statement st = c.createStatement()) {
            st.execute(sql);
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM books")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    isEmpty = true;
                }
            }
        }

        // Подключение выше уже закрыто, теперь можно спокойно добавлять книги
        if (isEmpty) {
            insert("Путь Абая", "Мухтар Ауэзов", 1942);
            insert("Кровь и пот", "Абдижамил Нурпеисов", 1970);
            insert("Кочевники", "Ильяс Есенберлин", 1976);
            insert("Мастер и Маргарита", "Михаил Булгаков", 1967);
            insert("Преступление и наказание", "Фёдор Достоевский", 1866);
            insert("Война и мир", "Лев Толстой", 1869);
            insert("1984", "Джордж Оруэлл", 1949);
            insert("Скотный двор", "Джордж Оруэлл", 1945);
            insert("451 градус по Фаренгейту", "Рэй Брэдбери", 1953);
            insert("Дюна", "Фрэнк Герберт", 1965);
            insert("Властелин колец", "Дж. Р. Р. Толкин", 1954);
            insert("Гарри Поттер и философский камень", "Дж. К. Роулинг", 1997);
            insert("Маленький принц", "Антуан де Сент-Экзюпери", 1943);
            insert("Три товарища", "Эрих Мария Ремарк", 1936);
            insert("Над пропастью во ржи", "Джером Сэлинджер", 1951);
            insert("Чистый код", "Роберт Мартин", 2008);
            insert("Effective Java", "Джошуа Блох", 2018);
            insert("Грокаем алгоритмы", "Адитья Бхаргава", 2016);
        }
    }



    public List<Book> findAll() throws SQLException {
        List<Book> result = new ArrayList<>();
        String sql = "SELECT id, title, author, year FROM books ORDER BY id";
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("year")
                ));
            }
        }
        return result;
    }

    public void insert(String title, String author, int year) throws SQLException {
        String sql = "INSERT INTO books(title, author, year) VALUES (?, ?, ?)";
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setInt(3, year);
            ps.executeUpdate();
        }
    }

    public int update(int id, String title, String author, int year) throws SQLException {
        String sql = "UPDATE books SET title=?, author=?, year=? WHERE id=?";
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setInt(3, year);
            ps.setInt(4, id);
            return ps.executeUpdate();
        }
    }

    public int delete(int id) throws SQLException {
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement("DELETE FROM books WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }
}
