package IDS;

import org.pcap4j.packet.Packet;

public interface Rule {
    boolean evaluate(Packet packet);
    int calculateSeverity(Packet packet);
}
