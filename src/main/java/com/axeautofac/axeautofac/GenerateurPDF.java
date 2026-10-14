package com.axeautofac.axeautofac;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GenerateurPDF {

    // --- LES COORDONNÉES LÉGALES DE VOTRE PÈRE ---
    private static final String NOM_GARAGE = "GARAGE GHULAM DIN";
    private static final String ADRESSE_GARAGE = "123 Route de la Mécanique\n75000 Ville";
    private static final String TELEPHONE_GARAGE = "06 00 00 00 00";
    private static final String SIRET_GARAGE = "SIRET : XXXXXXXXXXXXXX - RM de Goussainville";

    // On ajoute le taux de TVA (20% en général pour la mécanique)
    private static final double TAUX_TVA = 0.20;

    public static String creerFacture(String client, String immatriculation, String prestation, String montantHTStr) {
        try {
            // 0. Calculs TVA
            // On remplace la virgule par un point au cas où, puis on convertit en nombre
            double montantHT = Double.parseDouble(montantHTStr.replace(",", "."));
            double montantTVA = montantHT * TAUX_TVA;
            double montantTTC = montantHT + montantTVA;

            // 1. Localisation automatique du dossier "Documents"
            String cheminDocuments = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Factures_Garage";
            File dossier = new File(cheminDocuments);
            if (!dossier.exists()) {
                dossier.mkdirs();
            }

            // 2. Génération du nom de fichier unique
            String dateDuJour = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String dateAffichage = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String cheminComplet = cheminDocuments + File.separator + "Facture_" + immatriculation + "_" + dateDuJour + ".pdf";

            // 3. Initialisation de la feuille A4 virtuelle
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(cheminComplet));
            document.open();

            // 4. Choix des polices d'écriture
            Font policeTitre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22);
            Font policeNormale = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font policeGras = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            // --- EN-TÊTE DU GARAGE ---
            Paragraph entete = new Paragraph(NOM_GARAGE + "\n" + ADRESSE_GARAGE + "\nTél : " + TELEPHONE_GARAGE + "\n" + SIRET_GARAGE, policeGras);
            document.add(entete);
            document.add(new Paragraph("\n\n"));

            // --- TITRE DU DOCUMENT AVEC NUMÉRO UNIQUE ---
            String numeroFacture = "FAC-" + dateDuJour + "-" + immatriculation.replace("-", "");
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

            // Contenu tapé par votre père
            PdfPCell contenuPresta = new PdfPCell(new Phrase(prestation, policeNormale));
            contenuPresta.setPadding(10f);
            tablePrix.addCell(contenuPresta);

            // Affichage du montant HT formaté à 2 décimales
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

            // Ligne TTC
            PdfPCell cellTtcLibelle = new PdfPCell(new Phrase("Total TTC", policeGras));
            cellTtcLibelle.setBackgroundColor(Color.LIGHT_GRAY);
            tableTotal.addCell(cellTtcLibelle);

            PdfPCell cellTtcVal = new PdfPCell(new Phrase(String.format("%.2f €", montantTTC), policeGras));
            cellTtcVal.setBackgroundColor(Color.LIGHT_GRAY);
            cellTtcVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableTotal.addCell(cellTtcVal);

            document.add(tableTotal);

            // 5. Signature
            Paragraph footer = new Paragraph("\n\n\nMerci de votre confiance.", policeNormale);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

            return cheminComplet;

        } catch (NumberFormatException e) {
            // Si votre père tape "cent euros" au lieu de "100"
            System.err.println("Erreur de format de nombre : le montant doit être un chiffre.");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}