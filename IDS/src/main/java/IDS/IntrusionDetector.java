package IDS;

import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.IpV6Packet;
import org.pcap4j.packet.Packet;

import java.util.HashSet;
import java.util.Set;

public class IntrusionDetector {

    private final Set<String> blacklistedIPs = new HashSet<>();

    // Add an IP to the blacklist
    public void addBlacklistedIP(String ip) {
        blacklistedIPs.add(ip);
    }

    // Detect intrusion based on blacklisted IPs
    public void detectIntrusion(Packet packet) {
        String srcIp = extractSourceIP(packet);
        if (srcIp != null && blacklistedIPs.contains(srcIp)) {
            System.out.println("ALERT: Intrusion detected from IP " + srcIp);
            // Additional action can be added here, e.g., log the incident or block the packet
        }
    }

    // Extract source IP from packet
    private String extractSourceIP(Packet packet) {
        if (packet.contains(IpV4Packet.class)) {
            return packet.get(IpV4Packet.class).getHeader().getSrcAddr().getHostAddress();
        } else if (packet.contains(IpV6Packet.class)) {
            return packet.get(IpV6Packet.class).getHeader().getSrcAddr().getHostAddress();
        }
        return null; // Return null for non-IP packets
    }
}
