package IHM;

import model.Membre;
import rmi.BibliRemote;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ChatFrame extends JFrame {
    BibliRemote remote;
    Membre membre;
    JTextArea areaMessages;
    JTextField fieldMessage;
    JTextField fieldDestinataire;
    int lastSizePublic = 0;
    int lastSizePrive  = 0;

    // ✅ Constructeur normal
    public ChatFrame(BibliRemote remote, Membre membre) {
        this(remote, membre, "");
    }

    // ✅ Constructeur avec message pré-rempli
    public ChatFrame(BibliRemote remote, Membre membre, String msgPreRempli) {
        this.remote = remote;
        this.membre = membre;
        setTitle(" Chat — " + membre.getNom());
        setSize(520, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI(msgPreRempli);
        startReceiver();
    }

    private void initUI(String msgPreRempli) {
        // ── Panel haut ──
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(new Color(44, 62, 80));
        panelTop.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        JLabel title = new JLabel(" Chat — connecté en tant que : "
                + membre.getNom()
                + " (" + membre.getRole() + ")");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelTop.add(title, BorderLayout.WEST);

        // ── Zone messages ──
        areaMessages = new JTextArea();
        areaMessages.setEditable(false);
        areaMessages.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        areaMessages.setLineWrap(true);
        areaMessages.setWrapStyleWord(true);
        areaMessages.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane scroll = new JScrollPane(areaMessages);

        // ── Panel envoi ──
        JPanel panelBottom = new JPanel(new BorderLayout(8, 8));
        panelBottom.setBackground(new Color(44, 62, 80));
        panelBottom.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        // ✅ Champ destinataire — visible seulement pour admin
        JPanel panelDestinataire = new JPanel(new BorderLayout(5, 0));
        panelDestinataire.setBackground(new Color(44, 62, 80));

        fieldDestinataire = new JTextField();
        fieldDestinataire.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fieldDestinataire.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 120, 140)),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));

        JLabel lblDest = new JLabel("Destinataire:");
        lblDest.setForeground(Color.WHITE);
        lblDest.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        panelDestinataire.add(lblDest,           BorderLayout.WEST);
        panelDestinataire.add(fieldDestinataire, BorderLayout.CENTER);

        // ✅ Admin voit le champ destinataire pour répondre en privé
        if ("admin".equals(membre.getRole())) {
            panelBottom.add(panelDestinataire, BorderLayout.NORTH);
        }

        fieldMessage = new JTextField(msgPreRempli); // ← message pré-rempli
        fieldMessage.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldMessage.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 120, 140)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        JButton btnEnvoyer = new JButton("Envoyer ");
        btnEnvoyer.setBackground(new Color(39, 174, 96));
        btnEnvoyer.setForeground(Color.WHITE);
        btnEnvoyer.setFocusPainted(false);
        btnEnvoyer.setBorderPainted(false);
        btnEnvoyer.setFont(new Font("Segoe UI", Font.BOLD, 13));

        panelBottom.add(fieldMessage, BorderLayout.CENTER);
        panelBottom.add(btnEnvoyer,   BorderLayout.EAST);

        // ── Layout ──
        setLayout(new BorderLayout());
        add(panelTop,    BorderLayout.NORTH);
        add(scroll,      BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);

        // ── Actions ──
        btnEnvoyer.addActionListener(e -> envoyer());
        fieldMessage.addActionListener(e -> envoyer());
    }

    private void envoyer() {
        String msg  = fieldMessage.getText().trim();
        String dest = fieldDestinataire.getText().trim();

        if (msg.isEmpty()) return;

        try {
            //  Admin peut envoyer message privé
            if ("admin".equals(membre.getRole()) && !dest.isEmpty()) {
                remote.envoyerMessagePrive(membre.getNom(), dest, msg);

            } else {
                // Membre envoie message public
                remote.envoyerMessage(membre.getNom(), msg);
                areaMessages.append("Moi : " + msg + "\n");
            }
            fieldMessage.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur envoi: " + e.getMessage());
        }
    }

    // ── Thread réception ──
    private void startReceiver() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    // Messages publics
                    ArrayList<String> publics = remote.getMessages();
                    if (publics.size() > lastSizePublic) {
                        for (int i = lastSizePublic; i < publics.size(); i++) {
                            final String m = publics.get(i);
                            // Ne pas réafficher ses propres messages
                            if (!m.startsWith("[" + membre.getNom() + "]")) {
                                SwingUtilities.invokeLater(() -> {
                                    areaMessages.append(m + "\n");
                                    areaMessages.setCaretPosition(areaMessages.getDocument().getLength());
                                });
                            }
                        }
                        lastSizePublic = publics.size();
                    }

                    // Messages privés pour ce membre
                    ArrayList<String> prives = remote.getMessagesPour(membre.getNom());
                    if (prives.size() > lastSizePrive) {
                        for (int i = lastSizePrive; i < prives.size(); i++) {
                            final String m = prives.get(i);
                            SwingUtilities.invokeLater(() -> {
                                areaMessages.append( m + "\n");
                                areaMessages.setCaretPosition(
                                        areaMessages.getDocument().getLength());
                            });
                        }
                        lastSizePrive = prives.size();
                    }

                    Thread.sleep(800);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
}