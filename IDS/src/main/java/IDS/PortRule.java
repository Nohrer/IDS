package IDS;

import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;

public class PortRule implements Rule {
    private final int suspiciousPort;

    public PortRule(int suspiciousPort) {
        this.suspiciousPort = suspiciousPort;
    }

    @Override
    public boolean evaluate(Packet packet) {
        if (packet.contains(TcpPacket.class)) {
            int destPort = packet.get(TcpPacket.class).getHeader().getDstPort().valueAsInt();
            return destPort == suspiciousPort;
        }
        return false;
    }

}
