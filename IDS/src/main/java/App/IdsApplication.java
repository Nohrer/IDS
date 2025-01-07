package App;

import Controller.BlackListController;
import Controller.MainSceneController;
import Controller.PacketCaptureController;
import IDS.PacketReception;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class IdsApplication extends Application {
    private PacketReception packetReception = new PacketReception();
    private String css = getClass().getResource("/IDSfxml/styling.css").toExternalForm();

    @Override
    public void start(Stage primaryStage) {
        switchToMainScene(primaryStage); // Start with the main scene
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void switchToMainScene(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/IDSfxml/MainScene.fxml"));
            MainSceneController mainController = new MainSceneController(packetReception, this);
            loader.setController(mainController);

            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(css);
            stage.setTitle("Main Scene");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToPacketCapture(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/IDSfxml/PacketCaptureScene.fxml"));
            PacketCaptureController packetController = new PacketCaptureController(packetReception, this);
            loader.setController(packetController);

            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(css);
            stage.setTitle("Packet Capture");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToBlackList(Stage stage) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/IDSfxml/BlackList.fxml"));
            BlackListController blackListController = new BlackListController();
            blackListController.setApp(this);
            loader.setController(blackListController);

            Parent root = loader.load();

            Scene scene = new Scene(root);

            scene.getStylesheets().add(css);
            stage.setTitle("Black List");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}