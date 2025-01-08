package IDS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Notification {
    private int severity;
    private String description;
    private String srcIp;
    private int dstPort;
    private String date;
    public Notification() {
    }

    public Notification(int severity, String description, String srcIp, int dstPort, String date) {
        this.severity = severity;
        this.description = description;
        this.srcIp = srcIp;
        this.dstPort = dstPort;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "severity=" + severity +
                ", description='" + description + '\'' +
                ", srcIp='" + srcIp + '\'' +
                ", dstPort=" + dstPort +
                ", date='" + date + '\'' +
                '}';
    }

    public int getSeverity() { return severity; }
    public void setSeverity(int severity) { this.severity = severity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSrcIp() { return srcIp; }
    public void setSrcIp(String srcIp) { this.srcIp = srcIp; }

    public int getDstPort() { return dstPort; }
    public void setDstPort(int dstPort) { this.dstPort = dstPort; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
