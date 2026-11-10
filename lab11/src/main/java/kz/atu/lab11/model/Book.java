package kz.atu.lab11.model;

import javafx.beans.property.*;

public class Book {
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty author = new SimpleStringProperty();
    private final DoubleProperty rating = new SimpleDoubleProperty();

    public Book(String title, String author, double rating) {
        setTitle(title);
        setAuthor(author);
        setRating(rating);
    }

    public String getTitle() { return title.get(); }
    public void setTitle(String value) { title.set(value); }
    public StringProperty titleProperty() { return title; }

    public String getAuthor() { return author.get(); }
    public void setAuthor(String value) { author.set(value); }
    public StringProperty authorProperty() { return author; }

    public double getRating() { return rating.get(); }
    public void setRating(double value) { rating.set(value); }
    public DoubleProperty ratingProperty() { return rating; }
}