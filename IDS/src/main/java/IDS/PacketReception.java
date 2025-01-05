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
    private final List<Packet> capturedPackets = new ArrayList<>();
    private final List<PacketFilter> filters = new ArrayList<>();
    private PcapHandle handle; // Handle for packet capture
    private volatile boolean isCapturing = false;
    private volatile boolean stopCapture = false;
    private Thread captureThread;

    public PcapNetworkInterface getDevice() {
        return this.device;
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
    //n9dro ndiro nfs lblan
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
        // Clear the existing packet list

        // Start a new capture session
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

    // Extract source IP
    private String extractSource(Packet packet) {
        if (packet instanceof IpPacket) {
            return ((IpPacket) packet).getHeader().getSrcAddr().getHostAddress();
        } else if (packet instanceof EthernetPacket) {
            Packet payload = ((EthernetPacket) packet).getPayload();
            if (payload instanceof IpPacket) {
                return ((IpPacket) payload).getHeader().getSrcAddr().getHostAddress();
            }
        } else if (packet instanceof ArpPacket) {
            return ((ArpPacket) packet).getHeader().getSrcProtocolAddr().getHostAddress();
        }
        return "Unknown";
    }

    // Extract destination IP
    private String extractDestination(Packet packet) {
        if (packet instanceof IpPacket) {
            return ((IpPacket) packet).getHeader().getDstAddr().getHostAddress();
        } else if (packet instanceof EthernetPacket) {
            Packet payload = ((EthernetPacket) packet).getPayload();
            if (payload instanceof IpPacket) {
                return ((IpPacket) payload).getHeader().getDstAddr().getHostAddress();
            }
        } else if (packet instanceof ArpPacket) {
            return ((ArpPacket) packet).getHeader().getDstProtocolAddr().getHostAddress();
        }
        return "Unknown";
    }

    // Extract protocol
    private String extractProtocol(Packet packet) {
        if (packet instanceof IpPacket) {
            IpNumber protocol = ((IpPacket) packet).getHeader().getProtocol();
            return protocol != null ? protocol.toString() : "Unknown";
        } else if (packet instanceof TcpPacket) {
            return "TCP";
        } else if (packet instanceof UdpPacket) {
            return "UDP";
        } else if (packet instanceof ArpPacket) {
            return "ARP";
        }
        return "Unknown";
    }

    // Get captured packets
    public List<Packet> getCapturedPackets() {
        return capturedPackets;
    }
}
