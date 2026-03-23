package com.nextgen.medvault.Models;

public class MedicalHistory {

    String id;
    String title;
    String file_name;
    String date_time;
    String notes;
    String status;

    public MedicalHistory(String id, String title, String file_name, String date_time, String notes, String status) {
        this.id = id;
        this.title = title;
        this.file_name = file_name;
        this.date_time = date_time;
        this.notes = notes;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFile_name() {
        return file_name;
    }

    public String getDate_time() {
        return date_time;
    }

    public String getNotes() {
        return notes;
    }

    public String getStatus() {
        return status;
    }
}
