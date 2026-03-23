package com.nextgen.medvault.Models;

public class Reminder {

    private String id;
    private String title;
    private String desc;
    private String time;
    private String start_date;
    private String days;
    private String created_at;
    private String status;

    // Constructor
    public Reminder(String id,
                    String title,
                    String desc,
                    String time,
                    String start_date,
                    String days,
                    String created_at,
                    String status) {

        this.id = id;
        this.title = title;
        this.desc = desc;
        this.time = time;
        this.start_date = start_date;
        this.days = days;
        this.created_at = created_at;
        this.status = status;
    }

    // Getters

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
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

    public String getStatus() {
        return status;
    }

    // Setter (for marking reminder completed)

    public void setStatus(String status) {
        this.status = status;
    }
}