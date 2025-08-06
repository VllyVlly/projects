module org.example.gomoku {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens org.example.gomoku to javafx.fxml;
    exports org.example.gomoku;
}