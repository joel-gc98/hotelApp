module com.gui.hotelapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;

    requires atlantafx.base;
    requires org.controlsfx.controls;

    opens com.gui.hotelapp to javafx.fxml;
    exports com.gui.hotelapp;
}