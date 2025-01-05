package App;

import Controller.MainSceneController;
import Controller.PacketCaptureController;
import IDS.ConnectionTracker;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import IDS.PacketReception;
import java.io.IOException;

public class IdsApplication extends Application {
    private PacketReception packetReception = new PacketReception(); // Shared instance
    private ConnectionTracker connectionTracker=new ConnectionTracker();
    @Override
    public void start(Stage primaryStage) throws Exception {
        switchToMainScene(primaryStage); // Start with the main scene
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void switchToMainScene(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/IDSfxml/MainScene.fxml"));
        MainSceneController mainController = new MainSceneController(packetReception, this);
        loader.setController(mainController);

        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Main Scene");
        stage.setScene(scene);
        stage.show();
    }

    public void switchToPacketCapture(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/IDSfxml/PacketCaptureScene.fxml"));
        PacketCaptureController packetController = new PacketCaptureController(packetReception,connectionTracker);
        loader.setController(packetController);

        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Packet Capture");
        stage.setScene(scene);
        stage.show();
    }

}
