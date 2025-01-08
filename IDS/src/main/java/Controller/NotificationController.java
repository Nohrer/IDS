package Controller;

import App.IdsApplication;
import IDS.Notification;
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

public class NotificationController {
    private IdsApplication app;

    @FXML
    private TableView<Notification> notificationsTable;

    @FXML
    private TableColumn<Notification, LocalDate> dateColumn;

    @FXML
    private TableColumn<Notification, String> severityColumn;

    @FXML
    private TableColumn<Notification, String> descriptionColumn;

    private ObservableList<Notification> notificationsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize the columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        severityColumn.setCellValueFactory(new PropertyValueFactory<>("severity"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Load sample data
        loadSampleData();

        // Set the data to the table
        notificationsTable.setItems(notificationsList);
    }

    private void loadSampleData() {
        // Add sample notifications
        notificationsList.add(new Notification(LocalDate.now(), "High", "Sample high severity notification"));
        notificationsList.add(new Notification(LocalDate.now(), "Medium", "Sample medium severity notification"));
        notificationsList.add(new Notification(LocalDate.now(), "Low", "Sample low severity notification"));
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