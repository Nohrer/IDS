package IDS;

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
            return bannedIpAddresse.isIpBanned(srcIp);
        }
        return false;
    }

}
