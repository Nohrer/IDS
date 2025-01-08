package IDS;

import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class PortRule implements Rule {
    private final Set<Integer> suspiciousPorts;

    public PortRule(Set<Integer> suspiciousPorts) {
        this.suspiciousPorts = suspiciousPorts;
    }

    @Override
    public void evaluate(Packet packet, List<Notification> notifications) {
        if (packet.contains(TcpPacket.class) || packet.contains(UdpPacket.class)) {
            int destPort = extractDestinationPort(packet);

            if (suspiciousPorts.contains(destPort)) {
                int severity = calculateSeverity(packet);
                String srcIp = packet.get(org.pcap4j.packet.IpV4Packet.class).getHeader().getSrcAddr().getHostAddress();
                String description = generateDescription(destPort, "Port-based intrusion detected");
                LocalDate date = LocalDate.now();

                Notification notification = new Notification(severity, description, srcIp, destPort, date);
                notifications.addFirst(notification);
                //System.out.println(notification.toString());

            }
        }
    }

    public int calculateSeverity(Packet packet) {
        int severity = 1;

        if (packet.contains(TcpPacket.class)) {
            TcpPacket tcpPacket = packet.get(TcpPacket.class);
            int destPort = tcpPacket.getHeader().getDstPort().valueAsInt();

            switch (destPort) {
                case 21: // FTP
                case 23: // Telnet
                    severity = 4;
                    break;

                case 22: // SSH
                case 3389: // Remote Desktop Protocol
                    severity = 3;
                    break;

                case 80:  // HTTP
                case 443: // HTTPS
                    severity = 2;
                    break;

                case 53: // DNS
                    severity = 1;
                    break;

                default:
                    severity = 1;
            }

            if (packet.length() > 1000) {
                severity = 4;
            }

            if (tcpPacket.getHeader().getSyn() && !tcpPacket.getHeader().getAck()) {
                severity = Math.max(severity, 3);
            }
        }

        return severity;
    }

    private int extractDestinationPort(Packet packet) {
        if (packet.contains(TcpPacket.class)) {
            return packet.get(TcpPacket.class).getHeader().getDstPort().value();
        } else if (packet.contains(UdpPacket.class)) {
            return packet.get(UdpPacket.class).getHeader().getDstPort().value();
        }
        return -1; // No port information available
    }

    private String generateDescription(int destPort, String baseMessage) {
        switch (destPort) {
            case 21:
                return baseMessage + ": FTP traffic detected—potential risk of unsecured file transfer.";
            case 23:
                return baseMessage + ": Telnet traffic detected—potentially insecure remote access.";
            case 22:
                return baseMessage + ": SSH traffic detected—monitor for unusual activity.";
            case 3389:
                return baseMessage + ": Remote Desktop Protocol traffic detected—watch for unauthorized access.";
            case 80:
                return baseMessage + ": HTTP traffic detected—unencrypted web traffic.";
            case 443:
                return baseMessage + ": HTTPS traffic detected—secure web traffic.";
            case 53:
                return baseMessage + ": DNS traffic detected—potential for DNS-based attacks.";
            default:
                return baseMessage + ": Traffic on an unspecified port detected.";
        }
    }
}
