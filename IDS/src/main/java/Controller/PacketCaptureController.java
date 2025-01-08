package Controller;

import IDS.ConnectionTracker;
import IDS.PacketData;
import App.IdsApplication;
import IDS.PacketReception;
import IDS.TrafficCounter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PacketCaptureController {
    private IdsApplication app;
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

    @FXML
    private Text treathNumber;

    @FXML
    private Text notificationCount;

    @FXML
    private TextField filterInput;

    private ObservableList<PacketData> packetDataList = FXCollections.observableArrayList();
    private ObservableList<PacketData> filteredDataList = FXCollections.observableArrayList();

    public PacketCaptureController(PacketReception packetReception, IdsApplication app) {
        this.packetReception = packetReception;
        this.app = app;
    }

    @FXML
    public void initialize() {
        timeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTime()));
        sourceColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSource()));
        destinationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDestination()));
        protocolColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProtocol()));
        lengthColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLength()));

        packetTable.setItems(packetDataList);
        new Thread(() -> {
            try {
                packetReception.runCapture(packetDataList); // Pass ObservableList for updates
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Initialize treathNumber with the current number of notifications
    }

    @FXML
    public void onStopCapture(ActionEvent e){
        packetNumber.setText(String.valueOf(packetReception.packetNumber()));
        connexionNumber.setText(String.valueOf(packetReception.connexionNumber()));
        packetReception.stopCapture();

        // Update treathNumber after stopping capture
        updateThreatNumber();
    }

    @FXML
    public void onResumeCapture(ActionEvent e){
        try {
            packetReception.recapture(packetDataList); // Resume capturing packets
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void onBlackListView(MouseEvent e) {
        try {
            Stage stage = (Stage) packetTable.getScene().getWindow(); // Get the current stage
            app.switchToBlackList(stage); // Switch to BlackList scene
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void onNotificationsClick(MouseEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            app.switchToNotifications(stage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void onApplyFilter(ActionEvent event) {
        String filterText = filterInput.getText();
        if (filterText == null || filterText.isEmpty()) {
            packetTable.setItems(packetDataList);
        } else {
            filteredDataList.clear();
            for (PacketData packet : packetDataList) {
                if (packet.getSource().contains(filterText) ||
                        packet.getDestination().contains(filterText) ||
                        packet.getProtocol().contains(filterText) ||
                        packet.getTime().contains(filterText) ||
                        packet.getLength().contains(filterText)) {
                    filteredDataList.add(packet);
                }
            }
            packetTable.setItems(filteredDataList);
        }
    }

    private void updateThreatNumber() {
        treathNumber.setText(String.valueOf(packetReception.getNotificationCount()));
        notificationCount.setText(String.valueOf(packetReception.getNotificationCount()));
    }
}