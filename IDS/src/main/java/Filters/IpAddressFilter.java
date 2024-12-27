package Filters;

import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;

public class IpAddressFilter implements PacketFilter {
    private final String targetIp;

    public IpAddressFilter(String targetIp) {
        this.targetIp = targetIp;
    }
    @Override
    public boolean filter(Packet packet) {
        if (packet.contains(IpV4Packet.class)) {
            IpV4Packet ipv4Packet = packet.get(IpV4Packet.class);
            String srcIp = ipv4Packet.getHeader().getSrcAddr().getHostAddress();
            String dstIp = ipv4Packet.getHeader().getDstAddr().getHostAddress();
            return srcIp.equals(targetIp) || dstIp.equals(targetIp);
        }
        return false;
    }
}
