package IDS;

import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.IpV6Packet;
import org.pcap4j.packet.Packet;

import java.util.HashMap;
import java.util.Map;

public class TrafficCounter {

    private final Map<String, Integer> incomingPackets = new HashMap<>();
    private final Map<String, Integer> outgoingPackets = new HashMap<>();

    public void updateTraffic(Packet packet) {
        String srcIp = extractSourceIP(packet);
        String dstIp = extractDestinationIP(packet);

        if (srcIp != null) {
            // Increment outgoing for source IP
            outgoingPackets.put(srcIp, outgoingPackets.getOrDefault(srcIp, 0) + 1);
        }

        if (dstIp != null) {
            // Increment incoming for destination IP
            incomingPackets.put(dstIp, incomingPackets.getOrDefault(dstIp, 0) + 1);
        }
    }

    public Map<String, Integer> getIncomingTraffic() {
        return incomingPackets;
    }

    public Map<String, Integer> getOutgoingTraffic() {
        return outgoingPackets;
    }

    private String extractSourceIP(Packet packet) {
        if (packet.contains(IpV4Packet.class)) {
            return packet.get(IpV4Packet.class).getHeader().getSrcAddr().getHostAddress();
        } else if (packet.contains(IpV6Packet.class)) {
            return packet.get(IpV6Packet.class).getHeader().getSrcAddr().getHostAddress();
        }
        return null; // Return null for non-IP packets
    }

    private String extractDestinationIP(Packet packet) {
        if (packet.contains(IpV4Packet.class)) {
            return packet.get(IpV4Packet.class).getHeader().getDstAddr().getHostAddress();
        } else if (packet.contains(IpV6Packet.class)) {
            return packet.get(IpV6Packet.class).getHeader().getDstAddr().getHostAddress();
        }
        return null; // Return null for non-IP packets
    }
}
