package com.nextgen.medvault.Models;

public class MedReminder {

    private String id;
    private String medicine;
    private String dosage;
    private String time;
    private String start_date;
    private String days;
    private String created_at;

    public MedReminder(String id, String medicine, String dosage, String time, String start_date, String days, String created_at) {
        this.id = id;
        this.medicine = medicine;
        this.dosage = dosage;
        this.time = time;
        this.start_date = start_date;
        this.days = days;
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public String getMedicine() {
        return medicine;
    }

    public String getDosage() {
        return dosage;
    }

    public String getTime() {
        return time;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getDays() {
        return days;
    }

    public String getCreated_at() {
        return created_at;
    }
}
