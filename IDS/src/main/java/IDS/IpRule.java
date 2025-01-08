package IDS;

import org.pcap4j.packet.IpPacket;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

import java.util.Date;
import java.util.List;

public class IpRule implements Rule {
    private final BannedIpAddresse bannedIpAddresse;

    public IpRule() {
        String filePath = "C:\\Users\\Soufiane\\Documents\\ENSETM\\UH2 ENSETM S3\\Java\\IDS2\\IDS\\IDS\\src\\main\\resources\\blackList.txt";
        bannedIpAddresse = new BannedIpAddresse(filePath);
    }

    @Override
    public void evaluate(Packet packet, List<Notification> notifications) {
        if (packet.contains(org.pcap4j.packet.IpV4Packet.class)) {
            String srcIp = packet.get(org.pcap4j.packet.IpV4Packet.class).getHeader().getSrcAddr().getHostAddress();

            if (bannedIpAddresse.isIpBanned(srcIp)) {
                int severity = calculateSeverity(packet);
                String description = generateDescription(packet, "IP-based intrusion detected");
                String date = new Date().toString();
                int destPort = extractDestinationPort(packet);

                Notification notification = new Notification(severity, description, srcIp, destPort, date);
                notifications.addFirst(notification);
                System.out.println(notification.toString());
            }
        }
    }

    @Override
    public int calculateSeverity(Packet packet) {
        int severity = 1;

        if (packet.contains(IpPacket.class)) {
            IpPacket ipPacket = packet.get(IpPacket.class);
            String sourceIp = ipPacket.getHeader().getSrcAddr().getHostAddress();
            String destinationIp = ipPacket.getHeader().getDstAddr().getHostAddress();

            if (bannedIpAddresse.isIpBanned(sourceIp) || bannedIpAddresse.isIpBanned(destinationIp)) {
                severity = 4;
            } else if (isPrivateIp(sourceIp) && !isPrivateIp(destinationIp)) {
                severity = Math.max(severity, 3);
            } else if (isSuspiciousIpRange(sourceIp) || isSuspiciousIpRange(destinationIp)) {
                severity = Math.max(severity, 3);
            }
        }

        return severity;
    }

    private boolean isPrivateIp(String ipAddress) {
        return ipAddress.startsWith("10.") ||
                ipAddress.startsWith("192.168.") ||
                (ipAddress.startsWith("172.") && Integer.parseInt(ipAddress.split("\\.")[1]) >= 16 && Integer.parseInt(ipAddress.split("\\.")[1]) <= 31);
    }

    private boolean isSuspiciousIpRange(String ipAddress) {
        return ipAddress.startsWith("203.0.113.") ||
                ipAddress.startsWith("198.51.100.") ||
                ipAddress.startsWith("192.0.2.");
    }

    private int extractDestinationPort(Packet packet) {
        if (packet.contains(TcpPacket.class)) {
            return packet.get(TcpPacket.class).getHeader().getDstPort().value();
        } else if (packet.contains(UdpPacket.class)) {
            return packet.get(UdpPacket.class).getHeader().getDstPort().value();
        }
        return -1;
    }

    private String generateDescription(Packet packet, String baseMessage) {
        IpPacket ipPacket = packet.get(IpPacket.class);
        String sourceIp = ipPacket.getHeader().getSrcAddr().getHostAddress();
        String destinationIp = ipPacket.getHeader().getDstAddr().getHostAddress();

        if (bannedIpAddresse.isIpBanned(sourceIp) || bannedIpAddresse.isIpBanned(destinationIp)) {
            return baseMessage + ": Communication with a blacklisted IP detected.";
        } else if (isPrivateIp(sourceIp) && !isPrivateIp(destinationIp)) {
            return baseMessage + ": Traffic from a private IP to a public IP detected.";
        } else if (isSuspiciousIpRange(sourceIp) || isSuspiciousIpRange(destinationIp)) {
            return baseMessage + ": Traffic from a suspicious IP range detected.";
        }
        return baseMessage + ": No specific anomalies detected.";
    }
}
