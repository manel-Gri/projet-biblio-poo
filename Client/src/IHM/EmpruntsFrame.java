package IHM;

import model.Emprunt;
import model.Membre;
import rmi.BibliRemote;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class EmpruntsFrame extends JFrame {
    BibliRemote remote;
    Membre membre;
    JTable table;
    DefaultTableModel model;
    ArrayList<Emprunt> listeEmprunts = new ArrayList<>();
    boolean isAdmin;

    public EmpruntsFrame(BibliRemote remote, Membre membre) {
        this.remote  = remote;
        this.membre  = membre;
        this.isAdmin = "admin".equals(membre.getRole());

        // ✅ Titre selon le rôle
        setTitle(isAdmin ? "Tous les emprunts — Admin"
                : "Mes emprunts : " + membre.getNom());
        setSize(750, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ✅ Colonnes selon le rôle
        String[] colonnes = isAdmin
                ? new String[]{"ID", "Membre", "Livre", "Date emprunt", "Date retour", "Rendu"}
                : new String[]{"ID", "Livre",           "Date emprunt", "Date retour", "Rendu"};

        model = new DefaultTableModel(colonnes, 0) {
            public boolean isCellEditable(int ligne, int col) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // ✅ Bouton Rendre — seulement pour membre
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bottom.setBackground(new Color(236, 240, 241));

        if (!isAdmin) {
            JButton btnRendre = new JButton("Rendre ce livre");
            btnRendre.setBackground(new Color(39, 174, 96));
            btnRendre.setForeground(Color.WHITE);
            btnRendre.setFocusPainted(false);
            btnRendre.setBorderPainted(false);
            btnRendre.setFont(new Font("Segoe UI", Font.BOLD, 13));
            bottom.add(btnRendre);

            btnRendre.addActionListener(e -> {
                int ligne = table.getSelectedRow();
                if (ligne == -1) {
                    JOptionPane.showMessageDialog(this, "Sélectionnez un emprunt !");
                    return;
                }
                if (listeEmprunts.isEmpty() || ligne >= listeEmprunts.size()) {
                    JOptionPane.showMessageDialog(this, "Erreur de sélection !");
                    return;
                }
                Emprunt empruntSelectionne = listeEmprunts.get(ligne);
                new RendreFrame(remote, this, empruntSelectionne).setVisible(true);
            });
        }

        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        chargerEmprunts();
    }

    public void chargerEmprunts() {
        try {
            // ✅ Une seule condition
            listeEmprunts = isAdmin
                    ? remote.getEmprunts()
                    : remote.getEmpruntsParMembre(membre.getId());

            if (listeEmprunts == null) listeEmprunts = new ArrayList<>();
            model.setRowCount(0);

            for (Emprunt em : listeEmprunts) {
                if (isAdmin) {
                    model.addRow(new Object[]{
                            em.getId(), em.getNomMembre(), em.getTitreLivre(),
                            em.getDateEmprunt(), em.getDateRetour(),
                            em.isRendu() ? "✅ Oui" : "❌ Non"
                    });
                } else {
                    model.addRow(new Object[]{
                            em.getId(), em.getTitreLivre(),
                            em.getDateEmprunt(), em.getDateRetour(),
                            em.isRendu() ? "✅ Oui" : "❌ Non"
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage());
            listeEmprunts = new ArrayList<>();
        }
    }


    /*static void main() {
        Membre membreTest = new Membre(1,"test","test","test@gmail.com");
        new EmpruntsFrame(remote,membreTest).setVisible(true);
    }*/
}