package IDS;

import org.pcap4j.packet.Packet;

public class IpRule implements Rule {
    private final String blacklistedIp;

    public IpRule(String blacklistedIp) {
        this.blacklistedIp = blacklistedIp;
    }

    @Override
    public boolean evaluate(Packet packet) {
        if (packet.contains(org.pcap4j.packet.IpV4Packet.class)) {
            String srcIp = packet.get(org.pcap4j.packet.IpV4Packet.class).getHeader().getSrcAddr().getHostAddress();
            System.out.println("Nigga is " + blacklistedIp + "srcAddr is :"+ srcIp);
            return srcIp.equals(blacklistedIp);
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Block packets from IP: " + blacklistedIp;
    }
}
