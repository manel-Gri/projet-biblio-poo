package IHM;

import model.Emprunt;
import rmi.BibliRemote;
import javax.swing.*;
import java.awt.*;

public class RendreFrame extends JFrame {
    BibliRemote remote;
    EmpruntsFrame empruntsFrame;
    Emprunt emprunt;

    public RendreFrame(BibliRemote remote, EmpruntsFrame empruntsFrame, Emprunt emprunt) {
        this.remote = remote;
        this.empruntsFrame = empruntsFrame;
        this.emprunt = emprunt;
        setTitle("Rendre un livre");
        setSize(380, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // ── Titre ──
        JLabel title = new JLabel("Confirmer le retour", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(44, 62, 80));
        panel.add(title, BorderLayout.NORTH);

        // ── Info emprunt ──
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setBackground(new Color(236, 240, 241));

        JLabel lblLivre = new JLabel("📖 Livre : " + emprunt.getTitreLivre());
        lblLivre.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblEmprunt = new JLabel("📅 Emprunté le : " + emprunt.getDateEmprunt());
        lblEmprunt.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblRetour = new JLabel("⏰ À rendre le : " + emprunt.getDateRetour());
        lblRetour.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // ── couleur rouge si en retard ──
        if (emprunt.getDateRetour() != null &&
                emprunt.getDateRetour().before(new java.util.Date())) {
            lblRetour.setForeground(new Color(231, 76, 60));
            lblRetour.setText("⚠️ En retard depuis : " + emprunt.getDateRetour());
        }

        infoPanel.add(lblLivre);
        infoPanel.add(lblEmprunt);
        infoPanel.add(lblRetour);
        panel.add(infoPanel, BorderLayout.CENTER);

        // ── Boutons ──
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        southPanel.setBackground(new Color(236, 240, 241));

        JButton btnAnnuler = new JButton("Annuler");
        btnAnnuler.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnAnnuler.setFocusPainted(false);

        JButton btnConfirmer = new JButton("✅ Confirmer le retour");
        btnConfirmer.setBackground(new Color(39, 174, 96));
        btnConfirmer.setForeground(Color.WHITE);
        btnConfirmer.setFocusPainted(false);
        btnConfirmer.setBorderPainted(false);
        btnConfirmer.setFont(new Font("Segoe UI", Font.BOLD, 13));

        southPanel.add(btnAnnuler);
        southPanel.add(btnConfirmer);
        panel.add(southPanel, BorderLayout.SOUTH);

        add(panel);

        // ── Actions ──
        btnAnnuler.addActionListener(e -> dispose());

        btnConfirmer.addActionListener(e -> {
            try {
                if (remote.rendreLivre(emprunt.getId())) {
                    JOptionPane.showMessageDialog(this,
                            "✅ Livre rendu avec succès !",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);
                    empruntsFrame.chargerEmprunts(); // ← rafraîchir la liste
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            " Erreur lors du retour.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        " Erreur: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
    }
}