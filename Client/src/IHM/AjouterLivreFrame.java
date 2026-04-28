package IHM;

import model.Livre;
import rmi.BibliRemote;
import javax.swing.*;
import java.awt.*;

public class AjouterLivreFrame extends JFrame {
    static BibliRemote remote;
    static DashboardFrame dashboard;

    public AjouterLivreFrame(BibliRemote remote, DashboardFrame dashboard) {
        this.remote = remote;
        this.dashboard = dashboard;
        setTitle("Ajouter un livre");
        setSize(400, 320);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        //Titre + Séparateur
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(new Color(236, 240, 241));

        JLabel title = new JLabel("Nouveau Livre", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(44, 62, 80));



        northPanel.add(title, BorderLayout.NORTH);
        panel.add(northPanel, BorderLayout.NORTH);

        //

        JTextField fTitre  = new JTextField(16);
        JTextField fAuteur = new JTextField(16);
        JTextField fGenre  = new JTextField(16);
        JSpinner   fStock  = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)); //boutons + et -

        styleField(fTitre);
        styleField(fAuteur);
        styleField(fGenre);

        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        centerPanel.setBackground(new Color(236, 240, 241));

        // ligne Titre
        JPanel titreLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titreLine.setBackground(new Color(236, 240, 241));
        JLabel lblTitre = new JLabel("Titre :");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitre.setForeground(new Color(44, 62, 80));
        titreLine.add(lblTitre);
        titreLine.add(fTitre);

// ligne Auteur
        JPanel auteurLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        auteurLine.setBackground(new Color(236, 240, 241));
        JLabel lblAuteur = new JLabel("Auteur :");
        lblAuteur.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAuteur.setForeground(new Color(44, 62, 80));
        auteurLine.add(lblAuteur);
        auteurLine.add(fAuteur);

// ligne Genre
        JPanel genreLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        genreLine.setBackground(new Color(236, 240, 241));
        JLabel lblGenre = new JLabel("Genre :");
        lblGenre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblGenre.setForeground(new Color(44, 62, 80));
        genreLine.add(lblGenre);
        genreLine.add(fGenre);

// ligne Stock
        JPanel stockLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        stockLine.setBackground(new Color(236, 240, 241));
        JLabel lblStock = new JLabel("Stock :");
        lblStock.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStock.setForeground(new Color(44, 62, 80));
        stockLine.add(lblStock);
        stockLine.add(fStock);

        centerPanel.add(titreLine);
        centerPanel.add(auteurLine);
        centerPanel.add(genreLine);
        centerPanel.add(stockLine);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Boutons
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        southPanel.setBackground(new Color(236, 240, 241));

        JButton btnAnnuler = new JButton("Annuler");
        btnAnnuler.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton btnSave = new JButton("Enregistrer");
        btnSave.setBackground(new Color(39, 174, 96));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));

        southPanel.add(btnAnnuler);
        southPanel.add(btnSave);
        panel.add(southPanel, BorderLayout.SOUTH);

        add(panel);

        // les Actions
        btnAnnuler.addActionListener(e -> dispose());// dispose est une fonction qui supprimer le jframe du memoire

        btnSave.addActionListener(e -> {
            String titre  = fTitre.getText().trim();
            String auteur = fAuteur.getText().trim();
            String genre  = fGenre.getText().trim();
            int    stock  = (int) fStock.getValue();

            if (titre.isEmpty() || auteur.isEmpty() || genre.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez remplir tous les champs ",
                        "Champs manquants", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Livre livre = new Livre(0, titre, auteur, genre, stock);
                if (remote.ajouterLivre(livre)) {
                    JOptionPane.showMessageDialog(this,
                            "Livre ajouté avec succès !");
                    dashboard.chargerLivres(null);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de l'ajout.");}

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur: " + ex.getMessage());

            }
        });
    }


    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 190, 200)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

   /* static void main(String[] args) {
        new AjouterLivreFrame(remote,dashboard).setVisible(true);
    }*/
}