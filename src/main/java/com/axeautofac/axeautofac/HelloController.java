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


        // On demande au générateur de créer le PDF
        String cheminFichier = GenerateurPDF.creerFacture(client, immatriculation, prestation, montant);

        if (cheminFichier != null) {
            // Succès ! On affiche le chemin exact pour rassurer votre père
            afficherAlerte(Alert.AlertType.INFORMATION, "Facture générée avec succès",
                    "Le document a été créé dans ce dossier :\n\n" + cheminFichier);

            // Bonus : on vide les cases pour la prochaine facture
            champClient.clear();
            champImmatriculation.clear();
            champPrestation.clear();
            champMontant.clear();
        } else {
            // Échec (si le PDF était par exemple déjà ouvert en arrière-plan)
            afficherAlerte(Alert.AlertType.ERROR, "Erreur système",
                    "Impossible de créer le PDF. Vérifiez que la facture n'est pas déjà ouverte.");
        }
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