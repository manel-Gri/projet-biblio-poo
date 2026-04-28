package IHM;

import model.Membre;
import rmi.BibliRemote;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    static BibliRemote remote;

    public LoginFrame(BibliRemote remote) {
        this.remote = remote;
        setTitle(" Page Login");
        setSize(380, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        /*-----------------------------------------------*/
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(115, 141, 209));
        /*cette partie pour le mettre une border transparante*/
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Bienvenue au Bibliothèque");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        panel.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        centerPanel.setBackground(new Color(115, 141, 209));

        JPanel emailPanel = new JPanel(new FlowLayout());
        emailPanel.setBackground(new Color(115, 141, 209));
        JLabel mail = new JLabel("Email:");
        mail.setForeground(Color.WHITE);
        JTextField TxtEmail = new JTextField(15);
        emailPanel.add(mail);
        emailPanel.add(TxtEmail);

        JPanel passePanel = new JPanel(new FlowLayout());
        passePanel.setBackground(new Color(115, 141, 209));
        JLabel passe = new JLabel("Mot de passe:");
        passe.setForeground(Color.WHITE);
        JPasswordField Txtpasse = new JPasswordField(15); // ✅ PasswordField
        passePanel.add(passe);
        passePanel.add(Txtpasse);

        centerPanel.add(emailPanel);
        centerPanel.add(passePanel);
        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout());
        southPanel.setBackground(new Color(255, 255, 255));

        JButton btnLogin = new JButton("Se connecter");
        styleBtn(btnLogin, new Color(39, 174, 96));

        JButton btnInscription = new JButton("Créer un compte");
        styleBtn(btnInscription, new Color(52, 152, 219));

        southPanel.add(btnLogin);
        southPanel.add(btnInscription);
        panel.add(southPanel, BorderLayout.SOUTH);

        add(panel);

        btnLogin.addActionListener(e -> {
            try {
                String email = TxtEmail.getText().trim();
                String password = new String(Txtpasse.getPassword()).trim();

                if (email.isEmpty()|| password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Entrez votre email !");
                    return;
                }
                Membre m = remote.login(email,password);
                if (m != null) {
                    new DashboardFrame(remote, m).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, " Email introuvable !");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnInscription.addActionListener(e ->
                new InscriptionFrame(remote, this).setVisible(true));
    }

    private void styleBtn(JButton btn, Color c) {
        btn.setBackground(c);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }
   /* public static void main(String[] args) {
        new LoginFrame(remote).setVisible(true);
    }*/
}
