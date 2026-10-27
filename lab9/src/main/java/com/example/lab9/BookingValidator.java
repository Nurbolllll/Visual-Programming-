package com.example.lab9;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public final class BookingValidator {
    public static ValidationResult validate(String name, String roomType, String checkInStr, String checkOutStr) {
        Map<String, String> errors = new LinkedHashMap<>();

        // Проверка ФИО
        if (name == null || name.trim().length() < 2 || name.trim().length() > 80) {
            errors.put("name", "ФИО: от 2 до 80 символов");
        }

        // Проверка выбора номера
        if (roomType == null || roomType.isBlank()) {
            errors.put("roomType", "Выберите тип номера");
        }

        LocalDate checkIn = null;
        LocalDate checkOut = null;

        // Проверка даты заезда
        try {
            checkIn = LocalDate.parse(checkInStr.trim());
            if (checkIn.isBefore(LocalDate.now())) {
                errors.put("checkIn", "Дата заезда не может быть в прошлом");
            }
        } catch (Exception e) {
            errors.put("checkIn", "Введите дату в формате ГГГГ-ММ-ДД");
        }

        // Проверка даты выезда
        try {
            checkOut = LocalDate.parse(checkOutStr.trim());
            if (checkIn != null && (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn))) {
                errors.put("checkOut", "Дата выезда должна быть позже заезда");
            }
        } catch (Exception e) {
            errors.put("checkOut", "Введите дату в формате ГГГГ-ММ-ДД");
        }

        return new ValidationResult(errors);
    }
}