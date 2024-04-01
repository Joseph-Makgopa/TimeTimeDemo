module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens com.example.demo to javafx.fxml;
    opens com.example.demo.controllers to javafx.fxml;
    opens com.example.demo.models to javafx.fxml;
    exports com.example.demo;
    exports com.example.demo.models;
    exports com.example.demo.utilities;

}