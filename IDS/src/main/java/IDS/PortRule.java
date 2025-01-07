package IDS;

import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;

import java.util.Set;

public class PortRule implements Rule {
    private final Set<Integer> suspiciousPorts;

    public PortRule(Set<Integer> suspiciousPorts) {
        this.suspiciousPorts = suspiciousPorts;
    }

    @Override
    public boolean evaluate(Packet packet) {
        if (packet.contains(TcpPacket.class)) {
            int destPort = packet.get(TcpPacket.class).getHeader().getDstPort().valueAsInt();
            return suspiciousPorts.contains(destPort);
        }
        return false;
    }

    public int calculateSeverity(Packet packet) {
        int severity = 1;

        if (packet.contains(TcpPacket.class)) {
            TcpPacket tcpPacket = packet.get(TcpPacket.class);
            int destPort = tcpPacket.getHeader().getDstPort().valueAsInt();

            switch (destPort) {
                case 21: // FTP
                case 23: // Telnet
                    severity = 5;
                    break;

                case 22: // SSH
                case 3389: // Remote Desktop Protocol
                    severity = 4;
                    break;

                case 80:  // HTTP
                case 443: // HTTPS
                    severity = 3;
                    break;

                case 53: // DNS
                    severity = 2;
                    break;

                default:
                    severity = 1;
            }

            if (packet.length() > 1000) {
                severity = 5;
            }

            if (tcpPacket.getHeader().getSyn() && !tcpPacket.getHeader().getAck()) {
                severity = 4;
            }
        }

        if (!packet.contains(TcpPacket.class)) {
            severity = 2;
        }

        return severity;
    }


}
