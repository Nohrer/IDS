package IDS;

import java.time.LocalDate;

public class Notification {
    private int severity;
    private String description;
    private String srcIp;
    private int dstPort;
    private LocalDate date;

    public Notification() {
    }

    public Notification(int severity, String description, String srcIp, int dstPort, LocalDate date) {
        this.severity = severity;
        this.description = description;
        this.srcIp = srcIp;
        this.dstPort = dstPort;
        this.date = date;
    }


    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public int getDstPort() {
        return dstPort;
    }

    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Method to get severity description
    public String getSeverityDescription() {
        switch (severity) {
            case 0:
                return "Low";
            case 1:
                return "Moderate";
            case 2:
                return "High";
            case 3:
                return "Critical";
            case 4:
                return "Severe";
            default:
                return "Unknown";
        }
    }

    @Override
    public String toString() {
        return "Notification{" +
                "severity=" + getSeverityDescription() +
                ", description='" + description + '\'' +
                ", srcIp='" + srcIp + '\'' +
                ", dstPort=" + dstPort +
                ", date=" + date +
                '}';
    }
}