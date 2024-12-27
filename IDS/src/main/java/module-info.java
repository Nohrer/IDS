module com.example.ids {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.pcap4j.core;


    opens IDS to javafx.fxml;
    exports IDS;
    exports Filters;
    opens Filters to javafx.fxml;
    exports App;
    opens App to javafx.fxml;
    exports Controller;
    opens Controller to javafx.fxml;

}