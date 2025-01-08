package IDS;

import java.time.LocalDate;

public class Notification {
    private LocalDate date;
    private String severity;
    private String description;

    public Notification(LocalDate date, String severity, String description) {
        this.date = date;
        this.severity = severity;
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getSeverity() {
        return severity;
    }

    public String getDescription() {
        return description;
    }
}