package Controller;

import App.IdsApplication;
import IDS.BannedIpAddresse;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

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
    public void initialize() {
        // Initialize the BannedIpAddresse with a path to the blacklist file
        bannedIpAddresse = new BannedIpAddresse("/home/nohrer/Desktop/COOP/IDS/IDS/src/main/resources/blackList.txt");

        // Load the blacklisted IP addresses into the ListView
        blackListedIpList.getItems().addAll(bannedIpAddresse.getBlackList());
    }

    @FXML
    public void onAddBlackListedIp(ActionEvent event) {
        // Add IP to blacklist
        String ip = blackListedIpTextAdd.getText();
        if (ip != null && !ip.isEmpty()) {
            bannedIpAddresse.addIpToBlackList(ip);
            blackListedIpList.getItems().add(ip);
            blackListedIpTextAdd.clear();
        }
    }

    @FXML
    public void onRemoveBlackListedIp(ActionEvent event) {
        // Remove IP from blacklist
        String ip = blackListedIpRemove.getText();
        if (ip != null && !ip.isEmpty()) {
            bannedIpAddresse.removeIpFromBlackList(ip);
            blackListedIpList.getItems().remove(ip);
            blackListedIpRemove.clear();
        }
    }

    @FXML
    public void onDashboardClick(MouseEvent event) {
        System.out.println("Dashboard clicked");
        try {
            Stage stage = (Stage) blackListedIpList.getScene().getWindow(); // Get the current stage
            app.switchToPacketCapture(stage); // Switch to PacketCapture scene
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setApp(IdsApplication app) {
        this.app = app;
    }
}