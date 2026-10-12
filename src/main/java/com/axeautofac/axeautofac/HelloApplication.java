package com.axeautofac.axeautofac;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        // On agrandit la fenêtre par défaut à une taille raisonnable
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        // On active ENFIN le style Bootstrap pour que ça ait l'air professionnel
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        // On remplace votre fameux "Hello!"
        stage.setTitle("Facturation Garage");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}