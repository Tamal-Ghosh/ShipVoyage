module org.example.shipvoyage {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;

    opens org.example.shipvoyage to javafx.fxml;
    exports org.example.shipvoyage;
    exports org.example.shipvoyage.app;
    opens org.example.shipvoyage.app to javafx.fxml;
    exports org.example.shipvoyage.controller;
    opens org.example.shipvoyage.controller to javafx.fxml;
    opens org.example.shipvoyage.controller.admin to javafx.fxml;
}
