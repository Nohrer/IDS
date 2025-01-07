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




    public IntrusionDetector(){
        IpRule ipRule = new IpRule();
        rules.add(ipRule);
    }
    // Add an IP to the blacklist


    // Detect intrusion based on blacklisted IPs
    public void detectIntrusion(Packet packet) {
        for (Rule rule : rules) {
            if (rule.evaluate(packet)) {
                System.out.println("ALERT: Rule violation detected! ");
            }
        }

    }
}
