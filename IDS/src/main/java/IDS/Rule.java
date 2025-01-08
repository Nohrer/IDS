package IDS;

import org.pcap4j.packet.Packet;

import java.util.List;

public interface Rule {
    void evaluate(Packet packet, List<Notification> notifications);
    int calculateSeverity(Packet packet);
}
