package IHM;

import model.Livre;
import model.Membre;
import rmi.BibliRemote;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class DashboardFrame extends JFrame {
    static BibliRemote remote;
    static Membre membre;
    JTable tableLivres;
    DefaultTableModel modelTable;
    JTextField ZoneSearch;

    public DashboardFrame(BibliRemote remote, Membre membre) {
        this.remote = remote;
        this.membre = membre;
        setTitle(" Bibliothèque " + membre.getNom());
        setSize(800, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);



        // le menu qui appartient jlabel + search
        JPanel panelTop = new JPanel(new BorderLayout(10, 0));
        panelTop.setBackground(new Color(44, 62, 80));
        panelTop.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel title = new JLabel(" Catalogue des livres");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setBackground(new Color(44, 62, 80));
        ZoneSearch = new JTextField(16);
        JButton btnSearch = creerBtn(" Rechercher", new Color(52, 152, 219));
        searchPanel.add(ZoneSearch);
        searchPanel.add(btnSearch);

        panelTop.add(title, BorderLayout.WEST);
        panelTop.add(searchPanel, BorderLayout.EAST);

        // Tableau livres isCellEditable est une fonction overwriter exixte dans defaulttablemodel
        modelTable = new DefaultTableModel(
                new String[]{"ID", "Titre", "Auteur", "Genre", "Stock"}, 0) {
            public boolean isCellEditable(int Ligne, int Colonne) { return false; }
        };
        tableLivres = new JTable(modelTable);
        tableLivres.setRowHeight(28);
        tableLivres.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableLivres.getTableHeader().setBackground(new Color(52, 73, 94));
        tableLivres.getTableHeader().setForeground(Color.WHITE);
        tableLivres.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        JScrollPane scroll = new JScrollPane(tableLivres);

        // les Boutons
        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        panelBottom.setBackground(new Color(236, 240, 241));


        JButton btnEmprunter = new JButton("Emprunter");
        btnEmprunter.setBackground(new Color(39, 174, 96));
        btnEmprunter.setForeground(Color.WHITE);
        btnEmprunter.setFocusPainted(false);
        btnEmprunter.setBorderPainted(false);
        btnEmprunter.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton btnMesEmprunts = new JButton("Mes emprunts");
        btnMesEmprunts.setBackground(new Color(155, 89, 182));
        btnMesEmprunts.setForeground(Color.WHITE);
        btnMesEmprunts.setFocusPainted(false);
        btnMesEmprunts.setBorderPainted(false);
        btnMesEmprunts.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton btnAjouter = new JButton("Ajouter livre");
        btnAjouter.setBackground(new Color(52, 152, 219));
        btnAjouter.setForeground(Color.WHITE);
        btnAjouter.setFocusPainted(false);
        btnAjouter.setBorderPainted(false);
        btnAjouter.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton btnSupprimer = new JButton("Supprimer");
        btnSupprimer.setBackground(new Color(231, 76, 60));
        btnSupprimer.setForeground(Color.WHITE);
        btnSupprimer.setFocusPainted(false);
        btnSupprimer.setBorderPainted(false);
        btnSupprimer.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // ✅ Boutons visibles seulement pour l'admin
        if (membre.getRole().equals("admin")) {
            panelBottom.add(btnAjouter);
            panelBottom.add(btnSupprimer);
            JButton btnTousEmprunts = creerBtn("Tous les emprunts", new Color(230, 126, 34));
            panelBottom.add(btnTousEmprunts);
            btnTousEmprunts.addActionListener(e ->
                    new EmpruntsFrame(remote,membre).setVisible(true));

        } else {
            panelBottom.add(btnEmprunter);
            panelBottom.add(btnMesEmprunts);
        }

        setLayout(new BorderLayout());
        add(panelTop,    BorderLayout.NORTH);
        add(scroll,      BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);
        chargerLivres(null);


        btnSearch.addActionListener(e -> chargerLivres(ZoneSearch.getText().trim()));
        ZoneSearch.addActionListener(e -> chargerLivres(ZoneSearch.getText().trim()));

        btnEmprunter.addActionListener(e -> {
            int ligne = tableLivres.getSelectedRow();
            if (ligne == -1) {
                JOptionPane.showMessageDialog(this, "Sélectionnez un livre !");
                return;
            }
            int idLivre = (int) modelTable.getValueAt(ligne, 0);
            int stock   = (int) modelTable.getValueAt(ligne, 4);
            if (stock <= 0) {
                JOptionPane.showMessageDialog(this, "cette livre est hors stock !");
                return;
            }
            try {
                if (remote.emprunterLivre(idLivre, membre.getId())) {
                    JOptionPane.showMessageDialog(this, "Emprunt enregistré !");
                    chargerLivres(null);
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        btnMesEmprunts.addActionListener(e ->
                new EmpruntsFrame(remote, membre).setVisible(true));

        btnAjouter.addActionListener(e ->
                new AjouterLivreFrame(remote, this).setVisible(true));

        btnSupprimer.addActionListener(e -> {
            int ligne = tableLivres.getSelectedRow();
            if (ligne == -1) {
                JOptionPane.showMessageDialog(this, "Sélectionnez un livre !");
                return;
            }
            int id = (int) modelTable.getValueAt(ligne, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Supprimer ce livre ?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (remote.supprimerLivre(id)) {
                        JOptionPane.showMessageDialog(this, "Livre supprimé !");
                        chargerLivres(null);
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });
        JButton btnVoirLivres = new JButton(" Voir catalogue");
        btnVoirLivres.setBackground(new Color(52, 73, 94));
        btnVoirLivres.setForeground(Color.WHITE);
        btnVoirLivres.setFocusPainted(false);
        btnVoirLivres.setBorderPainted(false);
        panelBottom.add(btnVoirLivres);

        btnVoirLivres.addActionListener(e ->
                new LivresFrame(remote,membre).setVisible(true));
    }

    public void chargerLivres(String q) {
        try {
            ArrayList<Livre> list = (q == null || q.isEmpty())
                    ? remote.getLivres() : remote.rechercherLivre(q);
            modelTable.setRowCount(0);
            for (Livre l : list) {
                if (l.getStock() > 0) { // ← seulement les livres disponibles
                    modelTable.addRow(new Object[]{
                            l.getId(), l.getTitre(), l.getAuteur(),
                            l.getGenre(), l.getStock()
                    });
            }}
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage());
        }
    }

    private JButton creerBtn(String txt, Color c) {
        JButton btn = new JButton(txt);
        btn.setBackground(c);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return btn;
    }

   /* public static void main(String[] args) {

        Membre membreTest = new Membre(1,"test","test","test@gmail.com");
        new DashboardFrame(remote, membreTest).setVisible(true);
    }*/
}