package IDS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Notification {
    private String severity;
    private String description;
    private String srcIp;
    private int dstPort;
    private String date;
    public Notification() {
    }

    public Notification(String severity, String description, String srcIp, int dstPort, String date) {
        this.severity = severity;
        this.description = description;
        this.srcIp = srcIp;
        this.dstPort = dstPort;
        this.date = date;
    }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSrcIp() { return srcIp; }
    public void setSrcIp(String srcIp) { this.srcIp = srcIp; }

    public int getDstPort() { return dstPort; }
    public void setDstPort(int dstPort) { this.dstPort = dstPort; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
