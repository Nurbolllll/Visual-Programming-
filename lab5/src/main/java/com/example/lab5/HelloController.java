package com.example.lab5;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class HelloController {
    @FXML private TextField txtFullName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;

    @FXML private ToggleGroup paymentGroup;
    @FXML private ToggleGroup deliveryGroup;

    @FXML private CheckBox chkBox;
    @FXML private CheckBox chkBonusCard;
    @FXML private CheckBox chkAgreement;

    @FXML private Label lblResult;

    @FXML
    private void onCreateClick() {
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (fullName.isBlank() || email.isBlank() || phone.isBlank()) {
            showError("Заполните обязательные поля (ФИО, Email, Телефон).");
            return;
        }

        if (!isEmailValid(email)) {
            showError("Некорректный формат Email. Проверьте символы '@' и точку.");
            txtEmail.requestFocus();
            return;
        }

        RadioButton payment = (RadioButton) paymentGroup.getSelectedToggle();
        RadioButton delivery = (RadioButton) deliveryGroup.getSelectedToggle();

        if (payment == null || delivery == null) {
            showError("Выберите способ оплаты и вариант доставки.");
            return;
        }

        if (chkAgreement != null && !chkAgreement.isSelected()) {
            showError("Необходимо подтвердить согласие на обработку данных.");
            return;
        }

        String extras = buildExtras();

        lblResult.setText(
                "Карточка покупателя:\n" +
                        "ФИО: " + fullName + "\n" +
                        "Email: " + email + "\n" +
                        "Телефон: " + phone + "\n" +
                        "Оплата: " + payment.getText() + "\n" +
                        "Доставка: " + delivery.getText() + "\n" +
                        "Дополнительно: " + extras
        );
    }

    private boolean isEmailValid(String email) {
        int at = email.indexOf('@');
        int dot = email.lastIndexOf('.');
        return at > 0 && dot > at + 1 && dot < email.length() - 1;
    }

    private String buildExtras() {
        StringBuilder result = new StringBuilder();
        if (chkBox != null && chkBox.isSelected()) result.append("подарочная упаковка; ");
        if (chkBonusCard != null && chkBonusCard.isSelected()) result.append("бонусная карта; ");
        if (chkAgreement != null && chkAgreement.isSelected()) result.append("согласен; ");
        if (result.length() == 0) return "не выбрано";
        return result.toString();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка ввода");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onClearClick() {
        txtFullName.clear();
        txtEmail.clear();
        txtPhone.clear();
        if (paymentGroup.getSelectedToggle() != null) paymentGroup.getSelectedToggle().setSelected(false);
        if (deliveryGroup.getSelectedToggle() != null) deliveryGroup.getSelectedToggle().setSelected(false);
        if (chkBox != null) chkBox.setSelected(false);
        if (chkBonusCard != null) chkBonusCard.setSelected(false);
        if (chkAgreement != null) chkAgreement.setSelected(false);
        lblResult.setText("Результат:");
        txtFullName.requestFocus();
    }

    @FXML
    private void onExitClick() {
        Platform.exit();
    }
}