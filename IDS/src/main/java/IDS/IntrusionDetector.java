package IDS;

import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.IpV6Packet;
import org.pcap4j.packet.Packet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IntrusionDetector {

    private final List<Rule> rules = new ArrayList<>();

    private final Set<Integer> suspiciousPorts = Set.of(
           // 22,   // SSH
           // 23,   // Telnet
            //25,   // SMTP
            //53,   // DNS
            80   // HTTP
            //110,  // POP3
            //143,  // IMAP
            //443,  // HTTPS
            //445,  // SMB
            //3389  // RDP
    );


    public IntrusionDetector(){
        IpRule ipRule = new IpRule();
        PortRule portRule = new PortRule(suspiciousPorts);
        rules.add(ipRule);
        rules.add(portRule);
    }

    public void detectIntrusion(Packet packet) {
        for (Rule rule : rules) {
            if (rule.evaluate(packet)) {
                int severity = rule.calculateSeverity(packet);
                System.out.println("ALERT: Rule violation detected! Severity : "+ severity);
            }
        }

    }
}
