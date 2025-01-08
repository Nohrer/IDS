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
    private ConnectionTracker connectionTracker;
    private final List<Packet> capturedPackets = new ArrayList<>();
    private PcapHandle handle; // Handle for packet capture
    private volatile boolean isCapturing = false;
    private volatile boolean stopCapture = false;
    private Thread captureThread;
    private IntrusionDetector intrusionDetector=new IntrusionDetector();
    public PcapNetworkInterface getDevice() {
        return this.device;
    }
    public PacketReception() {
        this.connectionTracker = new ConnectionTracker();
    }
    private List<Notification> notifications = new ArrayList<>();

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setDevice(PcapNetworkInterface device) {
        if (device == null) {
            System.out.println("No device chosen");
            System.exit(1);
        }
        this.device = device;
    }

    // Display available network interfaces
    public List<PcapNetworkInterface> showAvailableInterface() {
        List<PcapNetworkInterface> interfaces = new ArrayList<>();
        try {
            interfaces = Pcaps.findAllDevs();
        } catch (Exception e) {
            System.err.println("Error while fetching network interfaces: " + e.getMessage());
        }
        return interfaces;
    }

    // Start packet capture
    public void runCapture(ObservableList<PacketData> packetDataList) {
        if (isCapturing) {
            System.out.println("Capture is already running.");
            return;
        }

        isCapturing = true;
        stopCapture = false;

        captureThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int snapshotLength = 65536;
                    int readTimeout = 50;
                    handle = device.openLive(snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout);

                    handle.loop(-1, new PacketListener() {
                        @Override
                        public void gotPacket(Packet packet) {

                            if (stopCapture) {
                                try {
                                    handle.breakLoop();
                                } catch (NotOpenException e) {
                                    System.err.println("Handle already closed: " + e.getMessage());
                                }
                                return;
                            }

                            // Process the packet
                            capturedPackets.add(packet);
                            connectionTracker.updateConnections(packet);
                            intrusionDetector.detectIntrusion(packet,notifications);
                            final String time = new Date().toString();
                            final String source = extractSource(packet);
                            final String destination = extractDestination(packet);
                            final String protocol = extractProtocol(packet);
                            final String length = String.valueOf(packet.length());

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    packetDataList.add(new PacketData(time, source, destination, protocol, length));

                                }
                            });
                        }
                    });
                } catch (InterruptedException e) {
                    System.out.println("Packet capture loop interrupted gracefully.");
                } catch (PcapNativeException | NotOpenException e) {
                    e.printStackTrace();
                } finally {
                    isCapturing = false;
                    if (handle != null && handle.isOpen()) {
                        handle.close();
                    }
                }
            }
        });

        captureThread.setDaemon(true);
        captureThread.start();
    }

    public void recapture(ObservableList<PacketData> packetDataList) {
        runCapture(packetDataList);
    }

    public void stopCapture() {
        if (!isCapturing) {
            System.out.println("No capture is currently running.");
            return;
        }

        stopCapture = true;
        isCapturing = false;

        if (handle != null && handle.isOpen()) {
            handle.close();
        }

        System.out.println("Capture stopped.");
    }
    //Retourne ConnectionNumber.

    public int connexionNumber(){
        return connectionTracker.getActiveConnectionCount();
    }
    public int packetNumber(){
        return capturedPackets.size();
    }

    // Extract source IP
    private String extractSource(Packet packet) {
        while (packet != null) {
            if (packet instanceof IpPacket) {
                return ((IpPacket) packet).getHeader().getSrcAddr().getHostAddress();
            } else if (packet instanceof ArpPacket) {
                return ((ArpPacket) packet).getHeader().getSrcProtocolAddr().getHostAddress();
            }
            // Move to the next layer of the packet (payload)
            packet = packet.getPayload();
        }
        return "Unknown";
    }

    // Extract destination IP
    private String extractDestination(Packet packet) {
        while (packet != null) {
            if (packet instanceof IpPacket) {
                return ((IpPacket) packet).getHeader().getDstAddr().getHostAddress();
            } else if (packet instanceof ArpPacket) {
                return ((ArpPacket) packet).getHeader().getDstProtocolAddr().getHostAddress();
            }
            // Move to the next layer of the packet (payload)
            packet = packet.getPayload();
        }
        return "Unknown";
    }


    private String extractProtocol(Packet packet) {
        while (packet != null) {
            if (packet instanceof TcpPacket) {
                TcpPacket tcpPacket = (TcpPacket) packet;
                TcpPacket.TcpHeader tcpHeader = tcpPacket.getHeader();
                int srcPort = tcpHeader.getSrcPort().valueAsInt();
                int dstPort = tcpHeader.getDstPort().valueAsInt();

                // Check for HTTP and HTTPS based on port numbers
                if (srcPort == 80 || dstPort == 80) {
                    return "HTTP";
                } else if (srcPort == 443 || dstPort == 443) {
                    return "HTTPS";
                }
                return "TCP";
            }
            // Check if it's a UDP packet
            else if (packet instanceof UdpPacket) {
                return "UDP";
            }
            // Check if it's an ARP packet
            else if (packet instanceof ArpPacket) {
                return "ARP";
            }
            else if (packet instanceof IpPacket) {
                IpPacket ipPacket = (IpPacket) packet;
                IpNumber protocol = ipPacket.getHeader().getProtocol();
                if(protocol.name().toString()=="TCP" && packet.get(TcpPacket.class).getHeader().getDstPort().valueAsInt() ==443)
                    return "HTTPS";
                else if (protocol.name().toString()=="TCP" && packet.get(TcpPacket.class).getHeader().getDstPort().valueAsInt() ==80) {
                    return "HTTP";
                }
                return protocol != null ? protocol.name().toString() : "Unknown IP Protocol";
            }

            // Move to the next layer of the packet (payload)
            packet = packet.getPayload();
        }
        return "Unknown Protocol";
    }




    public int getNotificationCount() {
        return notifications.size();
    }


    // Get captured packets
    public List<Packet> getCapturedPackets() {
        return capturedPackets;
    }


}
