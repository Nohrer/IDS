package Controller;

import App.IdsApplication;
import IDS.PacketData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import IDS.PacketReception;
import javafx.stage.Stage;
import org.pcap4j.core.PcapNetworkInterface;
import java.util.List;

public class MainSceneController {

    @FXML
    private ComboBox<String> interfaceList;

    @FXML
    private Button interfaceScanButton;

    private  PacketReception packetReception = new PacketReception();

    private IdsApplication app; // Reference to application for scene switching

    public MainSceneController(PacketReception packetReception, IdsApplication app) {
        this.packetReception = packetReception;
        this.app = app;
    }

    @FXML
    public void initialize() {
        List<PcapNetworkInterface> interfaces = packetReception.showAvailableInterface();
        if (interfaces != null && !interfaces.isEmpty()) {
            for (PcapNetworkInterface networkInterface : interfaces) {
                interfaceList.getItems().add(networkInterface.getName());
            }
        } else {
            interfaceList.getItems().add("No interfaces available");
        }
    }

    @FXML
    public void onInterfaceSelected(ActionEvent event) {
        String selectedInterfaceName = interfaceList.getSelectionModel().getSelectedItem();
        if (selectedInterfaceName != null) {
            try {
                List<PcapNetworkInterface> interfaces = packetReception.showAvailableInterface();
                PcapNetworkInterface selectedInterface = interfaces.stream()
                        .filter(netInterface -> netInterface.getName().equals(selectedInterfaceName))
                        .findFirst()
                        .orElse(null);

                if (selectedInterface != null) {
                    packetReception.setDevice(selectedInterface); // Update PacketReception
                    Stage stage = (Stage) interfaceScanButton.getScene().getWindow();
                    app.switchToPacketCapture(stage); // Switch scene
                } else {
                    System.out.println("Selected interface not found.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No interface selected.");
        }
    }



}
