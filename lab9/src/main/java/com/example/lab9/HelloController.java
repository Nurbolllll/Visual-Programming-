package com.example.lab9;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class HelloController {
    @FXML private TextField txtName;
    @FXML private ComboBox<String> cmbRoomType;
    @FXML private TextField txtCheckIn;
    @FXML private TextField txtCheckOut;

    @FXML private Label lblNameError;
    @FXML private Label lblRoomError;
    @FXML private Label lblCheckInError;
    @FXML private Label lblCheckOutError;

    @FXML private TableView<Reservation> tableReservations;
    @FXML private TableColumn<Reservation, String> colName;
    @FXML private TableColumn<Reservation, String> colRoom;
    @FXML private TableColumn<Reservation, LocalDate> colCheckIn;
    @FXML private TableColumn<Reservation, LocalDate> colCheckOut;

    private final ObservableList<Reservation> reservations = FXCollections.observableArrayList();
    private final PseudoClass INVALID_PSEUDO_CLASS = PseudoClass.getPseudoClass("invalid");

    @FXML
    public void initialize() {
        cmbRoomType.getItems().addAll("Стандарт", "Полулюкс", "Люкс");

        colName.setCellValueFactory(new PropertyValueFactory<>("guestName"));
        colRoom.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        colCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));

        tableReservations.setItems(reservations);
    }

    @FXML
    private void onRegister() {
        ValidationResult result = BookingValidator.validate(
                txtName.getText(),
                cmbRoomType.getValue(),
                txtCheckIn.getText(),
                txtCheckOut.getText()
        );

        showErrors(result);

        if (!result.isValid()) {
            new Alert(Alert.AlertType.WARNING, "Исправьте ошибки в форме").showAndWait();
            return;
        }

        try {
            Reservation item = new Reservation(
                    txtName.getText().trim(),
                    cmbRoomType.getValue(),
                    LocalDate.parse(txtCheckIn.getText().trim()),
                    LocalDate.parse(txtCheckOut.getText().trim())
            );
            reservations.add(item);
            onClear();
        } catch (RuntimeException e) {
            new Alert(Alert.AlertType.ERROR, "Не удалось завершить бронирование").showAndWait();
        }
    }

    @FXML
    private void onClear() {
        txtName.clear();
        cmbRoomType.setValue(null);
        txtCheckIn.clear();
        txtCheckOut.clear();
        showErrors(new ValidationResult(java.util.Collections.emptyMap()));
    }

    private void showErrors(ValidationResult r) {
        lblNameError.setText(r.error("name"));
        lblRoomError.setText(r.error("roomType"));
        lblCheckInError.setText(r.error("checkIn"));
        lblCheckOutError.setText(r.error("checkOut"));

        txtName.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, !r.error("name").isEmpty());
        cmbRoomType.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, !r.error("roomType").isEmpty());
        txtCheckIn.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, !r.error("checkIn").isEmpty());
        txtCheckOut.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, !r.error("checkOut").isEmpty());
    }
}