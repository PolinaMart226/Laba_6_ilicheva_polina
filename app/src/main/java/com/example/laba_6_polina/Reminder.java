package com.example.laba_6_polina;

public class Reminder {
    private final int id;
    private final String title;
    private final String message;
    private final String date;

    public Reminder(int id, String title, String message, String date) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }
}
