package Filters;

import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;

public class PortFilter implements PacketFilter {

    private final int targetPort;

    public PortFilter(int targetPort) {
        this.targetPort = targetPort;
    }
    @Override
    public boolean filter(Packet packet) {
        if (packet.contains(TcpPacket.class)) {
            TcpPacket tcpPacket = packet.get(TcpPacket.class);
            int srcPort = tcpPacket.getHeader().getSrcPort().valueAsInt();
            int dstPort = tcpPacket.getHeader().getDstPort().valueAsInt();
            return srcPort == targetPort || dstPort == targetPort;
        }
        return false;
    }
}
