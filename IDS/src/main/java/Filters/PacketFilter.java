package Filters;

import org.pcap4j.packet.Packet;

public interface PacketFilter {
    boolean filter(Packet packet);
}
