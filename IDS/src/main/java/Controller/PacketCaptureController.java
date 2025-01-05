package Controller;

import IDS.ConnectionTracker;
import IDS.PacketData;
import IDS.PacketReception;
import IDS.TrafficCounter;
import javafx.beans.value.ObservableIntegerValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;

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
    private Text packetNumber;

    @FXML
    private Text connexionNumber;

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
        setPacketNumber();
        new Thread(() -> {
            try {

                packetReception.runCapture(packetDataList); // Pass ObservableList for updates
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    @FXML
    public void onStopCapture(ActionEvent e){
        packetReception.stopCapture();
    }

    @FXML
    public void onResumeCapture(ActionEvent e){
        try {
            packetReception.recapture(packetDataList); // Resume capturing packets
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setPacketNumber() {
        if (packetDataList != null && packetNumber != null) {
            int size = packetDataList.size();
            packetNumber.setText(String.valueOf(size));
        } else {
            System.out.println("Error: packetDataList or packetNumber is null.");
        }
    }

}
