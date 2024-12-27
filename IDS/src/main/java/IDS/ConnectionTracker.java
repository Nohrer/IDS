package IDS;

import org.pcap4j.packet.Packet;

import java.util.HashSet;
import java.util.Set;

public class ConnectionTracker {
    private final Set<String> activeConnections = new HashSet<>();

    public void updateConnections(Packet packet) {
        String connectionKey = extractConnectionKey(packet); // "srcIP:srcPort-dstIP:dstPort"
        if (isTcpSyn(packet)) {
            activeConnections.add(connectionKey);
        } else if (isTcpFinOrRst(packet)) {
            activeConnections.remove(connectionKey);
        }
    }

    public int getActiveConnectionCount() {
        return activeConnections.size();
    }

    private String extractConnectionKey(Packet packet) {
        if (packet.contains(org.pcap4j.packet.TcpPacket.class)) {
            org.pcap4j.packet.TcpPacket tcpPacket = packet.get(org.pcap4j.packet.TcpPacket.class);
            String srcIp = packet.get(org.pcap4j.packet.IpV4Packet.class).getHeader().getSrcAddr().getHostAddress();
            String dstIp = packet.get(org.pcap4j.packet.IpV4Packet.class).getHeader().getDstAddr().getHostAddress();
            int srcPort = tcpPacket.getHeader().getSrcPort().valueAsInt();
            int dstPort = tcpPacket.getHeader().getDstPort().valueAsInt();

            return srcIp + ":" + srcPort + "-" + dstIp + ":" + dstPort;
        }
        return null; // Return null for non-TCP packets
    }
    private boolean isTcpSyn(Packet packet) {
        if (packet.contains(org.pcap4j.packet.TcpPacket.class)) {
            org.pcap4j.packet.TcpPacket tcpPacket = packet.get(org.pcap4j.packet.TcpPacket.class);
            return tcpPacket.getHeader().getSyn() && !tcpPacket.getHeader().getAck();
        }
        return false;
    }

    private boolean isTcpFinOrRst(Packet packet) {
        if (packet.contains(org.pcap4j.packet.TcpPacket.class)) {
            org.pcap4j.packet.TcpPacket tcpPacket = packet.get(org.pcap4j.packet.TcpPacket.class);
            return tcpPacket.getHeader().getFin() || tcpPacket.getHeader().getRst();
        }
        return false;
    }
}

