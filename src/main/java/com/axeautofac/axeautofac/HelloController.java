package com.axeautofac.axeautofac;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class HelloController {

    // Ces annotations @FXML sont les câbles qui relieront le code à l'interface visuelle
    @FXML
    private TextField champClient;

    @FXML
    private TextField champImmatriculation;

    @FXML
    private TextField champPrestation;

    @FXML
    private TextField champMontant;

    @FXML
    protected void onGenererFactureClick(ActionEvent event) {
        String client = champClient.getText();
        String prestation = champPrestation.getText();
        String montant = champMontant.getText();

        // On récupère la plaque, on enlève les espaces et on met en majuscules
        String immatriculation = champImmatriculation.getText().replaceAll("\\s+", "").toUpperCase();

        // Magie : si ça ressemble au format SIV (2 lettres, 3 chiffres, 2 lettres sans tiret)
        if (immatriculation.matches("^[A-Z]{2}\\d{3}[A-Z]{2}$")) {
            immatriculation = immatriculation.substring(0, 2) + "-" +
                    immatriculation.substring(2, 5) + "-" +
                    immatriculation.substring(5, 7);

            // On met à jour l'affichage dans la case pour que votre père voie la correction
            champImmatriculation.setText(immatriculation);
        }

        if (client.isEmpty() || immatriculation.isEmpty() || prestation.isEmpty() || montant.isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Erreur de saisie", "Veuillez remplir tous les champs avant de générer la facture, s'il vous plaît.");
            return;
        }

        System.out.println("=== NOUVELLE FACTURE ===");
        System.out.println("Client      : " + client);
        System.out.println("Immat.      : " + immatriculation);
        System.out.println("Prestation  : " + prestation);
        System.out.println("Total       : " + montant + " €");
        System.out.println("========================");

        afficherAlerte(Alert.AlertType.INFORMATION, "Données validées",
                "Les informations ont été récupérées avec succès !\nProchaine étape : générer le fichier PDF dans le dossier.");
    }

    // Un petit utilitaire pour afficher des boîtes de dialogue (pop-ups) à l'écran
    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
}