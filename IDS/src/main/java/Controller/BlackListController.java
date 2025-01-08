package Controller;

import App.IdsApplication;
import IDS.BannedIpAddresse;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class BlackListController {
    private IdsApplication app;

    @FXML
    private TextField blackListedIpTextAdd;

    @FXML
    private TextField blackListedIpRemove;

    @FXML
    private ListView<String> blackListedIpList;

    private BannedIpAddresse bannedIpAddresse;

    @FXML
    public void initialize() throws URISyntaxException {
        // Initialize the BannedIpAddresse with a path to the blacklist file
        URL fileUrl = getClass().getResource("/blackList.txt");
        if (fileUrl != null) {
            File file = Paths.get(fileUrl.toURI()).toFile();
            bannedIpAddresse = new BannedIpAddresse(file.getPath());
            // Load the blacklisted IP addresses into the ListView
            blackListedIpList.getItems().addAll(bannedIpAddresse.getBlackList());
        } else {
            showAlert("Error", "Blacklist file not found.");
        }
    }

    @FXML
    public void onAddBlackListedIp(ActionEvent event) {
        // Add IP to blacklist
        String ip = blackListedIpTextAdd.getText();
        if (ip != null && !ip.isEmpty()) {
            bannedIpAddresse.addIpToBlackList(ip);
            blackListedIpList.getItems().add(ip);
            blackListedIpTextAdd.clear();
            showAlert("Success", "The IP address has been added to the blacklist.");
        } else {
            showAlert("Error", "The IP address cannot be empty.");
        }
    }

    @FXML
    public void onRemoveBlackListedIp(ActionEvent event) {
        // Remove IP from blacklist
        String ip = blackListedIpRemove.getText();
        if (ip != null && !ip.isEmpty()) {
            if (bannedIpAddresse.isIpBanned(ip)) {
                bannedIpAddresse.removeIpFromBlackList(ip);
                blackListedIpList.getItems().remove(ip);
                blackListedIpRemove.clear();
                showAlert("Success", "The IP address has been removed from the blacklist.");
            } else {
                showAlert("Error", "The IP address is not in the blacklist.");
            }
        } else {
            showAlert("Error", "The IP address cannot be empty.");
        }
    }

    @FXML
    public void onDashboardClick(MouseEvent event) {
        try {
            Stage stage = (Stage) blackListedIpList.getScene().getWindow(); // Get the current stage
            app.switchToPacketCapture(stage); // Switch to PacketCapture scene
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
    public void setApp(IdsApplication app) {
        this.app = app;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}