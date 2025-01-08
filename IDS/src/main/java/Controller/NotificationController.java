package Controller;

import App.IdsApplication;
import IDS.Notification;
import IDS.PacketReception;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class NotificationController {
    private IdsApplication app;
    private List<Notification> notifications;
    @FXML
    private TableView<Notification> notificationsTable;

    @FXML
    private TableColumn<Notification, LocalDate> dateColumn;

    @FXML
    private TableColumn<Notification, String> severityColumn;

    @FXML
    private TableColumn<Notification, Integer> portNumber;

    @FXML
    private TableColumn<Notification, String> srcIp;

    @FXML
    private TableColumn<Notification, String> descriptionColumn;

    private ObservableList<Notification> notificationsList = FXCollections.observableArrayList();

    public NotificationController(PacketReception packetReception) {
        notifications = packetReception.getNotifications();
    }

    @FXML
    public void initialize() {
        // Initialize the columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        severityColumn.setCellValueFactory(new PropertyValueFactory<>("severityDescription"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        portNumber.setCellValueFactory(new PropertyValueFactory<>("dstPort"));
        srcIp.setCellValueFactory(new PropertyValueFactory<>("srcIp"));

        // Load data from PacketReception
        loadData();

        // Set the data to the table
        notificationsTable.setItems(notificationsList);
    }

    private void loadData() {
        // Add notifications from PacketReception to the observable list
        notificationsList.addAll(notifications);
    }

    @FXML
    public void onDashboardClick(MouseEvent event) {
        System.out.println("Dashboard clicked");
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            app.switchToPacketCapture(stage); // Switch to PacketCapture scene
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void onBlackListClick(MouseEvent e) {
        try {
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            app.switchToBlackList(stage); // Switch to BlackList scene
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setApp(IdsApplication app) {
        this.app = app;
    }
}