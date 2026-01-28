module org.example.arabicsearchengine {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.arabicsearchengine to javafx.fxml;
    exports org.example.arabicsearchengine;
    exports org.example.arabicsearchengine.controllers;
    opens org.example.arabicsearchengine.controllers to javafx.fxml;
}