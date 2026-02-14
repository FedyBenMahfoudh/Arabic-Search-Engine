module org.example.arabicsearchengine {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.graphics;
    requires java.logging;

    opens org.example.arabicsearchengine to javafx.fxml;
    exports org.example.arabicsearchengine;
    exports org.example.arabicsearchengine.controllers;
    opens org.example.arabicsearchengine.controllers to javafx.fxml;
}