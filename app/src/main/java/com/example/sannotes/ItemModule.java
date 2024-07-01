package com.example.sannotes;

public class ItemModule {
    private String title;
    private String notes;
    private String date;

    public ItemModule(String title, String notes, String date) {
        this.title = title;
        this.notes = notes;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
