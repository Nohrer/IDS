package Filters;

import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;

public class TcpPacketFilter implements PacketFilter {
    @Override
    public boolean filter(Packet packet) {
        return packet.contains(TcpPacket.class);
    }
}
