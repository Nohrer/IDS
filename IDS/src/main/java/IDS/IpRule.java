package IDS;

import org.pcap4j.packet.IpPacket;
import org.pcap4j.packet.Packet;

public class IpRule implements Rule {
    private BannedIpAddresse bannedIpAddresse;

    public IpRule(){

        String filePath="/home/nohrer/Desktop/COOP/IDS/IDS/src/main/resources/blackList.txt";
        bannedIpAddresse=new BannedIpAddresse(filePath);
    }
    @Override
    public boolean evaluate(Packet packet) {
        if (packet.contains(org.pcap4j.packet.IpV4Packet.class)) {
            String srcIp = packet.get(org.pcap4j.packet.IpV4Packet.class).getHeader().getSrcAddr().getHostAddress();
            //return bannedIpAddresse.isIpBanned(srcIp);
            //2
        }
        return false;
    }

    @Override
    public int calculateSeverity(Packet packet) {
        int severity = 1;

        if (packet.contains(IpPacket.class)) {
            IpPacket ipPacket = packet.get(IpPacket.class);
            String sourceIp = ipPacket.getHeader().getSrcAddr().getHostAddress();
            String destinationIp = ipPacket.getHeader().getDstAddr().getHostAddress();

            if (bannedIpAddresse.isIpBanned(sourceIp) || bannedIpAddresse.isIpBanned(destinationIp)) {
                severity = 5;
            }

            if (isPrivateIp(sourceIp) && !isPrivateIp(destinationIp)) {
                severity = Math.max(severity, 4);
            }

            if (isSuspiciousIpRange(sourceIp) || isSuspiciousIpRange(destinationIp)) {
                severity = Math.max(severity, 4);
            }
        }

        return severity;
    }

    private boolean isPrivateIp(String ipAddress) {
        return ipAddress.startsWith("10.") ||
                ipAddress.startsWith("192.168.") ||
                ipAddress.startsWith("172.") && (Integer.parseInt(ipAddress.split("\\.")[1]) >= 16 && Integer.parseInt(ipAddress.split("\\.")[1]) <= 31);
    }

    private boolean isSuspiciousIpRange(String ipAddress) {
        return ipAddress.startsWith("203.0.113.") ||
                ipAddress.startsWith("198.51.100.") ||
                ipAddress.startsWith("192.0.2.");
    }

}
