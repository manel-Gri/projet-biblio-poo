package IHM;

import model.Membre;
import rmi.BibliRemote;
import javax.swing.*;
import java.awt.*;

public class InscriptionFrame extends JFrame {
    static BibliRemote remote;
    LoginFrame loginFrame;

    public InscriptionFrame(BibliRemote remote, LoginFrame loginFrame) {
        this.remote = remote;
        this.loginFrame = loginFrame;
        setTitle("Créer un compte");
        setSize(400, 320);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Titre
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(new Color(236, 240, 241));

        JLabel title = new JLabel("Nouveau Compte", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(44, 62, 80));

        northPanel.add(title, BorderLayout.NORTH);
        panel.add(northPanel, BorderLayout.NORTH);

        //
        JTextField fNom    = new JTextField(16);
        JTextField fPrenom = new JTextField(16);
        JTextField fEmail  = new JTextField(16);
        JPasswordField fpassword = new JPasswordField(16); ;

        styleField(fNom);
        styleField(fPrenom);
        styleField(fEmail);
        styleField(fpassword);

        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        centerPanel.setBackground(new Color(236, 240, 241));

        // ligne Nom
        JPanel nomLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        nomLine.setBackground(new Color(236, 240, 241));
        JLabel lblnom = new JLabel("Nom :");
        lblnom.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblnom.setForeground(new Color(44, 62, 80));
        nomLine.add(lblnom);
        nomLine.add(fNom);

        // ligne Prénom
        JPanel prenomLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        prenomLine.setBackground(new Color(236, 240, 241));
        JLabel lblprenom = new JLabel("Prénom :");
        lblprenom.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblprenom.setForeground(new Color(44, 62, 80));
        prenomLine.add(lblprenom);
        prenomLine.add(fPrenom);

        // ligne Email
        JPanel emailLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        emailLine.setBackground(new Color(236, 240, 241));
        JLabel lblmail = new JLabel("Email :");
        lblmail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblmail.setForeground(new Color(44, 62, 80));
        emailLine.add(lblmail);
        emailLine.add(fEmail);


        // ligne password
        JPanel passeLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        passeLine.setBackground(new Color(236, 240, 241));
        JLabel lblpasse = new JLabel("password :");
        lblpasse.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblpasse.setForeground(new Color(44, 62, 80));
        passeLine.add(lblpasse);
        passeLine.add(fpassword);

        centerPanel.add(nomLine);
        centerPanel.add(prenomLine);
        centerPanel.add(emailLine);
        centerPanel.add(passeLine);
        panel.add(centerPanel, BorderLayout.CENTER);

        // les Boutons
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        southPanel.setBackground(new Color(236, 240, 241));

        JButton btnAnnuler = new JButton("Annuler");
        btnAnnuler.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton btnSave = new JButton("Créer le compte");
        btnSave.setBackground(new Color(52, 152, 219));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));

        southPanel.add(btnAnnuler);
        southPanel.add(btnSave);
        panel.add(southPanel, BorderLayout.SOUTH);

        add(panel);

        // Actions
        btnAnnuler.addActionListener(e -> dispose());

        btnSave.addActionListener(e -> {
            String nom    = fNom.getText().trim();
            String prenom = fPrenom.getText().trim();
            String email  = fEmail.getText().trim();
            String password  = new String(fpassword.getPassword()).trim();

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()||password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez remplir tous les champs !");
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(this,
                        "Email invalide !");
                return;
            }

            try {
                Membre m = new Membre(0, nom, prenom, email,password,"membre");
                if (remote.ajouterMembre(m)) {
                    JOptionPane.showMessageDialog(this,
                            "Compte créé avec succès !");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Cet email est déjà utilisé !");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur: " + ex.getMessage());
                ex.printStackTrace();
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

   /* static void main() {

        LoginFrame loginframe = null;
        new InscriptionFrame(remote,loginframe).setVisible(true);
    }*/
}