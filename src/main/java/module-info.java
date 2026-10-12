module com.axeautofac.axeautofac {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.axeautofac.axeautofac to javafx.fxml;
    exports com.axeautofac.axeautofac;
}