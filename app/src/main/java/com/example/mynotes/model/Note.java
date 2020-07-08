package com.example.mynotes.model;

public class Note {
   private String title,note,created,updated;
private Boolean mark;
    public Note(String title, String note, String created,String updated,Boolean bookmark) {
        this.title = title;
        this.note = note;
        this.created = created;
        mark=bookmark;
        this.updated=updated;
    }

    public Note() {
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public Boolean getMark() {
        return mark;
    }

    public void setMark(Boolean mark) {
        this.mark = mark;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
