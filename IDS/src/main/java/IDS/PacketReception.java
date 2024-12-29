package IDS;

import Filters.PacketFilter;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.IpNumber;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PacketReception {
    private PcapNetworkInterface device = null;
    private List<Packet> capturedPackets = new ArrayList<>();
    private final List<PacketFilter> filters = new ArrayList<>();
    private volatile boolean stopCapture = false;  // Flag to stop capture

    public PcapNetworkInterface getDevice(){
        return this.device;
    }

    public List<PcapNetworkInterface> showAvailableInterface(){
        List<PcapNetworkInterface> interfaces= new ArrayList<>();
        try {
            interfaces=Pcaps.findAllDevs();

        }catch (Exception e){
            System.err.println("Error while fetching network interfaces: " + e.getMessage());
        }
        return interfaces;
    }

    public void setDevice(PcapNetworkInterface device){
        if(device ==null){
            System.out.println("No device chosen");
            System.exit(1);
        }
        this.device = device;
    }

    public void runCapture(ObservableList<PacketData> packetDataList) throws PcapNativeException, NotOpenException, InterruptedException {
        if (device == null) {
            System.err.println("No device selected. Cannot start capture.");
            return;
        }

        int snapshotLength = 65536;
        int readTimeout = 100;
        PcapHandle handle = device.openLive(snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout);

        stopCapture = false; // Ensure the flag is reset before starting
        handle.loop(-1, new PacketListener() {
            @Override
            public void gotPacket(Packet packet) {
                if (stopCapture) {
                    try {
                        handle.breakLoop(); // Stop the loop when `stopCapture` is true
                    } catch (NotOpenException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                // Store the raw packet for future filtering
                capturedPackets.add(packet);

                // Create PacketData for display
                String time = new Date().toString();
                String source = extractSource(packet);
                String destination = extractDestination(packet);
                String protocol = extractProtocol(packet);
                String length = String.valueOf(packet.length());

                // Update the UI safely
                Platform.runLater(() -> {
                    packetDataList.add(new PacketData(time, source, destination, protocol, length));
                });
            }
        });

        handle.close();
    }


    public void resumeCapture(ObservableList<PacketData> packetDataList) {
        stopCapture = false; // Reset the flag
        try {
            runCapture(packetDataList); // Restart the capture loop
        } catch (PcapNativeException | NotOpenException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String extractSource(Packet packet) {
        if (packet == null) {
            return "Unknown";
        }

        // Handle IP packets (both IPv4 and IPv6)
        if (packet instanceof IpPacket) {
            IpPacket ipPacket = (IpPacket) packet;
            return ipPacket.getHeader().getSrcAddr().getHostAddress();
        }

        // Handle packets encapsulated in Ethernet frames
        if (packet instanceof EthernetPacket) {
            Packet payload = ((EthernetPacket) packet).getPayload();
            if (payload instanceof IpPacket) {
                IpPacket ipPacket = (IpPacket) payload;
                return ipPacket.getHeader().getSrcAddr().getHostAddress();
            }
        }

        // Handle ARP packets
        if (packet instanceof ArpPacket) {
            ArpPacket arpPacket = (ArpPacket) packet;
            return arpPacket.getHeader().getSrcProtocolAddr().getHostAddress();
        }

        return "Unknown";
    }

    private String extractDestination(Packet packet) {
        if (packet == null) {
            return "Unknown";
        }

        // Handle IP packets (both IPv4 and IPv6)
        if (packet instanceof IpPacket) {
            IpPacket ipPacket = (IpPacket) packet;
            return ipPacket.getHeader().getDstAddr().getHostAddress();
        }

        // Handle packets encapsulated in Ethernet frames
        if (packet instanceof EthernetPacket) {
            Packet payload = ((EthernetPacket) packet).getPayload();
            if (payload instanceof IpPacket) {
                IpPacket ipPacket = (IpPacket) payload;
                return ipPacket.getHeader().getDstAddr().getHostAddress();
            }
        }

        // Handle ARP packets
        if (packet instanceof ArpPacket) {
            ArpPacket arpPacket = (ArpPacket) packet;
            return arpPacket.getHeader().getDstProtocolAddr().getHostAddress();
        }

        return "Unknown";
    }

    private String extractProtocol(Packet packet) {
        if (packet == null) {
            return "Unknown";
        }

        // First check if it's an Ethernet packet with payload
        if (packet instanceof EthernetPacket) {
            Packet payload = ((EthernetPacket) packet).getPayload();
            if (payload != null) {
                return extractProtocolFromPayload(payload);
            }
        }

        // Handle protocols for IP packets (both IPv4 and IPv6)
        if (packet instanceof IpPacket) {
            IpPacket ipPacket = (IpPacket) packet;
            IpNumber protocol = ipPacket.getHeader().getProtocol();
            if (protocol != null) {
                String protocolName = protocol.toString();
                // Debugging logs for the protocol extraction
                System.out.println("Extracted Protocol: " + protocolName);
                if (protocolName.contains("TCP")) {
                    return "TCP";
                } else if (protocolName.contains("UDP")) {
                    return "UDP";
                } else if (protocolName.contains("ICMP")) {
                    return "ICMP";
                } else {
                    return protocolName;
                }
            }
        }

        // Specific protocol checks
        if (packet instanceof TcpPacket) {
            return "TCP";
        }
        if (packet instanceof UdpPacket) {
            return "UDP";
        }
        if (packet instanceof ArpPacket) {
            return "ARP";
        }

        // Log the unknown case for debugging
        System.out.println("Unknown Protocol for Packet: " + packet);
        return "Unknown";
    }


    private String extractProtocolFromPayload(Packet packet) {
        // Handle IP packets
        if (packet instanceof IpPacket) {
            IpPacket ipPacket = (IpPacket) packet;
            IpNumber protocol = ipPacket.getHeader().getProtocol();

            // Instead of switch, use if-else
            if (protocol != null) {
                String protocolName = protocol.toString();
                if (protocolName.contains("TCP")) {
                    return "TCP";
                } else if (protocolName.contains("UDP")) {
                    return "UDP";
                } else if (protocolName.contains("ICMP")) {
                    return "ICMP";
                } else {
                    return protocolName;
                }
            }
        }

        // Specific protocol checks
        if (packet instanceof TcpPacket) {
            return "TCP";
        }
        if (packet instanceof UdpPacket) {
            return "UDP";
        }
        if (packet instanceof ArpPacket) {
            return "ARP";
        }

        return "Unknown";
    }
    public void stopCapture() {
        stopCapture = true;
    }


    public List<Packet> getCapturedPackets() {
        return capturedPackets;
    }


}
