module com.test.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.dither.demo to javafx.fxml;
    exports com.dither.demo;
}