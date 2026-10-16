package com.axeautofac.axeautofac;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class GenerateurPDF {

    // --- LES COORDONNÉES LÉGALES OFFICIELLES DU GARAGE ---
    private static final String NOM_GARAGE = "L'AXE AUTO 94";
    private static final String STATUT_GARAGE = "SASU au capital de 1 000 €";
    private static final String ADRESSE_GARAGE = "97 Avenue des Martyrs de Chateaubriant\n94310 Orly";
    private static final String TELEPHONE_GARAGE = "06 00 00 00 00";
    private static final String SIRET_GARAGE = "SIREN : 106 724 644 - R.C.S. Créteil";

    // Taux de TVA (20%)
    private static final double TAUX_TVA = 0.20;

    public static String creerFacture(String client, String immatriculation, String prestation, String montantTTCStr) {
        try {
            // 0. Calculs comptables
            double montantTTC = Double.parseDouble(montantTTCStr.replace(",", "."));
            double montantHT = montantTTC / (1 + TAUX_TVA);
            double montantTVA = montantTTC - montantHT;

            // 1. Localisation automatique du dossier "Documents"
            String cheminDocuments = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Factures_Garage";
            File dossier = new File(cheminDocuments);
            if (!dossier.exists()) {
                dossier.mkdirs();
            }

            // 2. Génération du nom de fichier unique
            String dateDuJour = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String heureExacte = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
            String dateAffichage = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            String nomFichier = "Facture_" + immatriculation + "_" + dateDuJour + "_" + heureExacte + ".pdf";
            String cheminComplet = cheminDocuments + File.separator + nomFichier;

            // 3. Initialisation de la feuille A4 virtuelle
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(cheminComplet));
            document.open();

            // 4. Choix des polices d'écriture
            Font policeTitre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22);
            Font policeNormale = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font policeGras = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font policeLegale = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10);

            // --- INTÉGRATION DU LOGO ET DE L'EN-TÊTE ---
            // On utilise un tableau sans bordures pour aligner le logo à gauche et le texte à droite
            PdfPTable tableEntete = new PdfPTable(2);
            tableEntete.setWidthPercentage(100);
            tableEntete.setWidths(new float[]{1f, 2f}); // La colonne texte est 2x plus large que l'image

            // A. Chargement de l'image depuis les ressources (Dossier img tel que défini)
            PdfPCell cellLogo = new PdfPCell();
            cellLogo.setBorder(Rectangle.NO_BORDER);
            try {
                // IMPORTANT : L'image doit s'appeler exactement "logo.jpg" dans votre dossier img
                InputStream is = GenerateurPDF.class.getResourceAsStream("img/logo.jpg");
                if (is != null) {
                    byte[] imageBytes = is.readAllBytes();
                    Image logo = Image.getInstance(imageBytes);
                    // On redimensionne l'image pour qu'elle s'intègre proprement sans tout écraser
                    logo.scaleToFit(150, 150);
                    logo.setAlignment(Element.ALIGN_LEFT);
                    cellLogo.addElement(logo);
                } else {
                    System.out.println("Attention : Logo introuvable dans le dossier resources/com/axeautofac/axeautofac/img/");
                }
            } catch (Exception imgEx) {
                System.out.println("Erreur lors du chargement de l'image : " + imgEx.getMessage());
            }

            // B. Texte de l'en-tête
            PdfPCell cellTexte = new PdfPCell();
            cellTexte.setBorder(Rectangle.NO_BORDER);
            cellTexte.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph texteEntete = new Paragraph(NOM_GARAGE + " - " + STATUT_GARAGE + "\n" + ADRESSE_GARAGE + "\nTél : " + TELEPHONE_GARAGE + "\n" + SIRET_GARAGE, policeGras);
            texteEntete.setAlignment(Element.ALIGN_RIGHT);
            cellTexte.addElement(texteEntete);

            // Ajout au tableau principal d'en-tête
            tableEntete.addCell(cellLogo);
            tableEntete.addCell(cellTexte);
            document.add(tableEntete);
            document.add(new Paragraph("\n\n"));

            // --- TITRE DU DOCUMENT AVEC NUMÉRO UNIQUE ---
            String numeroFacture = "FAC-" + dateDuJour + "-" + heureExacte;
            Paragraph titre = new Paragraph("FACTURE N° " + numeroFacture, policeTitre);
            titre.setAlignment(Element.ALIGN_CENTER);
            document.add(titre);

            Paragraph dateParagraphe = new Paragraph("Date : " + dateAffichage, policeNormale);
            dateParagraphe.setAlignment(Element.ALIGN_RIGHT);
            document.add(dateParagraphe);
            document.add(new Paragraph("\n\n"));

            // --- INFORMATIONS DU CLIENT ---
            PdfPTable tableInfos = new PdfPTable(2);
            tableInfos.setWidthPercentage(100);

            PdfPCell cellClient = new PdfPCell(new Phrase("Client : " + client, policeNormale));
            cellClient.setBorder(Rectangle.NO_BORDER);

            PdfPCell cellVehicule = new PdfPCell(new Phrase("Véhicule (Immat.) : " + immatriculation, policeNormale));
            cellVehicule.setBorder(Rectangle.NO_BORDER);
            cellVehicule.setHorizontalAlignment(Element.ALIGN_RIGHT);

            tableInfos.addCell(cellClient);
            tableInfos.addCell(cellVehicule);
            document.add(tableInfos);
            document.add(new Paragraph("\n\n"));

            // --- LE TABLEAU DES PRESTATIONS ---
            PdfPTable tablePrix = new PdfPTable(2);
            tablePrix.setWidthPercentage(100);
            tablePrix.setWidths(new float[]{3f, 1f});

            // Titres des colonnes
            PdfPCell cellPresta = new PdfPCell(new Phrase("Désignation de la prestation", policeGras));
            cellPresta.setBackgroundColor(Color.LIGHT_GRAY);
            cellPresta.setPadding(8f);
            tablePrix.addCell(cellPresta);

            PdfPCell cellMontant = new PdfPCell(new Phrase("Montant HT (€)", policeGras));
            cellMontant.setBackgroundColor(Color.LIGHT_GRAY);
            cellMontant.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellMontant.setPadding(8f);
            tablePrix.addCell(cellMontant);

            // Contenu tapé
            PdfPCell contenuPresta = new PdfPCell(new Phrase(prestation, policeNormale));
            contenuPresta.setPadding(10f);
            tablePrix.addCell(contenuPresta);

            // Affichage du montant HT calculé, formaté à 2 décimales
            PdfPCell contenuMontant = new PdfPCell(new Phrase(String.format("%.2f €", montantHT), policeNormale));
            contenuMontant.setHorizontalAlignment(Element.ALIGN_CENTER);
            contenuMontant.setPadding(10f);
            tablePrix.addCell(contenuMontant);

            document.add(tablePrix);
            document.add(new Paragraph("\n\n"));

            // --- TOTAL (HT, TVA, TTC) ---
            PdfPTable tableTotal = new PdfPTable(2);
            tableTotal.setWidthPercentage(50);
            tableTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableTotal.setWidths(new float[]{1f, 1f});

            // Ligne HT
            tableTotal.addCell(new PdfPCell(new Phrase("Total HT", policeNormale)));
            PdfPCell cellHtVal = new PdfPCell(new Phrase(String.format("%.2f €", montantHT), policeNormale));
            cellHtVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableTotal.addCell(cellHtVal);

            // Ligne TVA
            tableTotal.addCell(new PdfPCell(new Phrase("TVA (20%)", policeNormale)));
            PdfPCell cellTvaVal = new PdfPCell(new Phrase(String.format("%.2f €", montantTVA), policeNormale));
            cellTvaVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableTotal.addCell(cellTvaVal);

            // Ligne TTC (Celui saisi dans l'interface)
            PdfPCell cellTtcLibelle = new PdfPCell(new Phrase("Total TTC", policeGras));
            cellTtcLibelle.setBackgroundColor(Color.LIGHT_GRAY);
            tableTotal.addCell(cellTtcLibelle);

            PdfPCell cellTtcVal = new PdfPCell(new Phrase(String.format("%.2f €", montantTTC), policeGras));
            cellTtcVal.setBackgroundColor(Color.LIGHT_GRAY);
            cellTtcVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableTotal.addCell(cellTtcVal);

            document.add(tableTotal);

            // 5. Signature et bas de page légal
            Paragraph footer = new Paragraph("\n\n\nMerci de votre confiance.", policeNormale);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            Paragraph mentionsLegales = new Paragraph(NOM_GARAGE + " - " + STATUT_GARAGE + " - " + SIRET_GARAGE, policeLegale);
            mentionsLegales.setAlignment(Element.ALIGN_CENTER);
            document.add(new Paragraph("\n"));
            document.add(mentionsLegales);

            document.close();

            return cheminComplet;

        } catch (NumberFormatException e) {
            System.err.println("Erreur de format de nombre : le montant doit être un chiffre.");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}