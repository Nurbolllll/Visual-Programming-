package com.example.lab9;

import java.time.LocalDate;

public class Reservation {
    private String guestName;
    private String roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    public Reservation(String guestName, String roomType, LocalDate checkInDate, LocalDate checkOutDate) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
}