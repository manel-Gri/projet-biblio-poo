package IHM;

import model.Livre;
import model.Membre;
import rmi.BibliRemote;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class LivresFrame extends JFrame {
    BibliRemote remote;
    JTable table;
    DefaultTableModel model;
    JTextField zoneSearch;
    ArrayList<Livre> listeLivres = new ArrayList<>();
    Membre membre;

    public LivresFrame(BibliRemote remote ,Membre membre) {
        this.remote = remote;
        this.membre = membre;
        setTitle(" Catalogue des livres");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        // ── Panel haut ──
        JPanel panelTop = new JPanel(new BorderLayout(10, 0));
        panelTop.setBackground(new Color(44, 62, 80));
        panelTop.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel title = new JLabel(" Tous les livres");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));

        // Barre de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setBackground(new Color(44, 62, 80));
        zoneSearch = new JTextField(16);

        JButton btnSearch = new JButton(" Rechercher");
        btnSearch.setBackground(new Color(52, 152, 219));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));

        searchPanel.add(zoneSearch);
        searchPanel.add(btnSearch);

        panelTop.add(title, BorderLayout.WEST);
        panelTop.add(searchPanel, BorderLayout.EAST);

        // ── Tableau ──
        model = new DefaultTableModel(
                new String[]{"ID", "Titre", "Auteur", "Genre", "Stock", "Disponible"}, 0) {
            public boolean isCellEditable(int ligne, int colonne) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionBackground(new Color(174, 214, 241));

        // Colonne Stock colorée selon disponibilité
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    int stock = (int) model.getValueAt(row, 4);
                    if (stock <= 0) {
                        setBackground(new Color(255, 220, 220)); // rouge clair si indispo
                    } else {
                        setBackground(Color.WHITE);
                    }
                }
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);

        // ── Panel bas ──
        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        panelBottom.setBackground(new Color(236, 240, 241));

        JButton btnRefresh = new JButton(" Rafraîchir");
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton btnFermer = new JButton("Fermer");
        btnFermer.setFocusPainted(false);
        btnFermer.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        panelBottom.add(btnRefresh);
        panelBottom.add(btnFermer);

        // ── Layout ──
        setLayout(new BorderLayout());
        add(panelTop,    BorderLayout.NORTH);
        add(scroll,      BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);

        // ── Actions ──
        btnSearch.addActionListener(e -> chargerLivres(zoneSearch.getText().trim()));
        zoneSearch.addActionListener(e -> chargerLivres(zoneSearch.getText().trim()));
        btnRefresh.addActionListener(e -> {
            zoneSearch.setText("");
            chargerLivres(null);
        });
        btnFermer.addActionListener(e -> dispose());

        JButton btnDemander = new JButton(" Demander ce livre");
        btnDemander.setBackground(new Color(231, 76, 60));
        btnDemander.setForeground(Color.WHITE);
        btnDemander.setFocusPainted(false);
        btnDemander.setBorderPainted(false);
        btnDemander.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelBottom.add(btnDemander);

        btnDemander.addActionListener(e -> {
            int ligne = table.getSelectedRow();

            // Pas de sélection → chat vide
            if (ligne == -1) {
                new ChatFrame(remote, membre).setVisible(true);
                return;
            }

            int stock = (int) model.getValueAt(ligne, 4);

            // Livre disponible → pas besoin de demander
            if (stock > 0) {
                JOptionPane.showMessageDialog(this,
                        "Ce livre est disponible — allez l'emprunter dans le Dashboard !");
                return;
            }

            // Livre indispo → chat avec message pré-rempli
            String titre  = (String) model.getValueAt(ligne, 1);
            String auteur = (String) model.getValueAt(ligne, 2);
            String msgPreRempli = "Demande : Je souhaite emprunter \""
                    + titre + "\" de " + auteur
                    + " (actuellement indisponible)";

            new ChatFrame(remote, membre, msgPreRempli).setVisible(true);
        });
        JButton btnModifierStock = new JButton(" Modifier stock");
        btnModifierStock.setBackground(new Color(230, 126, 34));
        btnModifierStock.setForeground(Color.WHITE);
        btnModifierStock.setFocusPainted(false);
        btnModifierStock.setBorderPainted(false);
        btnModifierStock.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelBottom.add(btnModifierStock);
        if (membre.getRole().equals("admin")) {
        btnModifierStock.addActionListener(e -> modifierStock());
        }
        chargerLivres(null);
    }

    private void modifierStock() {
        int ligne = table.getSelectedRow();
        if (ligne == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un livre !");
            return;
        }

        int    idLivre     = listeLivres.get(ligne).getId();
        String titreLivre  = (String) model.getValueAt(ligne, 1);
        int    stockActuel = (int)    model.getValueAt(ligne, 4);

        //  Dialog saisie nouveau stock
        JPanel dialogPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialogPanel.add(new JLabel("Livre : " + titreLivre));
        dialogPanel.add(new JLabel("Stock actuel : " + stockActuel));

        JSpinner spinnerStock = new JSpinner(
                new SpinnerNumberModel(stockActuel, 0, 1000, 1));
        JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        spinnerPanel.add(new JLabel("Nouveau stock :"));
        spinnerPanel.add(spinnerStock);
        dialogPanel.add(spinnerPanel);

        int result = JOptionPane.showConfirmDialog(
                this, dialogPanel,
                "Modifier le stock", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int nouveauStock = (int) spinnerStock.getValue();
            try {
                if (remote.modifierStock(idLivre, nouveauStock)) {
                    JOptionPane.showMessageDialog(this,
                            " Stock mis à jour : " + nouveauStock);
                    chargerLivres(null);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Erreur mise à jour !");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public void chargerLivres(String q) {
        try {
            listeLivres = (q == null || q.isEmpty())
                    ? remote.getLivres()
                    : remote.rechercherLivre(q);

            model.setRowCount(0);
            for (Livre l : listeLivres) {
                model.addRow(new Object[]{
                        l.getId(),
                        l.getTitre(),
                        l.getAuteur(),
                        l.getGenre(),
                        l.getStock(),
                        l.getStock() > 0 ? " Disponible" : " Indisponible"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage());
        }
    }


}