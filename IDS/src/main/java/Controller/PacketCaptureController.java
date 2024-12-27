package Controller;

import IDS.PacketData;
import IDS.PacketReception;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PacketCaptureController {

    private PacketReception packetReception;

    @FXML
    private TableView<PacketData> packetTable;

    @FXML
    private TableColumn<PacketData, String> timeColumn;

    @FXML
    private TableColumn<PacketData, String> sourceColumn;

    @FXML
    private TableColumn<PacketData, String> destinationColumn;

    @FXML
    private TableColumn<PacketData, String> protocolColumn;

    @FXML
    private TableColumn<PacketData, String> lengthColumn;

    @FXML
    private Button stopCaptureButton;

    private ObservableList<PacketData> packetDataList = FXCollections.observableArrayList();

    public PacketCaptureController(PacketReception packetReception) {
        this.packetReception = packetReception;
    }

    @FXML
    public void initialize() {
        timeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTime()));
        sourceColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSource()));
        destinationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDestination()));
        protocolColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProtocol()));
        lengthColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLength()));

        packetTable.setItems(packetDataList);

        // Start capturing packets in a separate thread
        new Thread(() -> {
            try {
                packetReception.runCapture(packetDataList); // Pass ObservableList for updates
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void onStopCapture() {
        System.out.println("Capture stopped.");
        packetReception.stopCapture();  // Call stopCapture to stop the capture
    }

}
