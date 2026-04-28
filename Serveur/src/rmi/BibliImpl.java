package rmi;

import database.DatabaseConnection;
import model.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class BibliImpl extends UnicastRemoteObject implements BibliRemote {
    Connection con;

    public BibliImpl() throws RemoteException {
        con = DatabaseConnection.makeConnection();
    }

    @Override
    public ArrayList<Livre> getLivres() throws RemoteException {
        ArrayList<Livre> list = new ArrayList<>();
        try {
            ResultSet rs = con.createStatement()
                    .executeQuery("SELECT * FROM livre");
            while (rs.next()) {
                list.add(new Livre(
                        rs.getInt("id"), rs.getString("titre"),
                        rs.getString("auteur"), rs.getString("genre"),
                        rs.getInt("stock")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public ArrayList<Livre> rechercherLivre(String q) throws RemoteException {
        ArrayList<Livre> list = new ArrayList<>();
        String sql = "SELECT * FROM livre WHERE titre LIKE ? OR auteur LIKE ? OR genre LIKE ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            String p = "%" + q + "%";
            ps.setString(1, p); ps.setString(2, p); ps.setString(3, p);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Livre(
                        rs.getInt("id"), rs.getString("titre"),
                        rs.getString("auteur"), rs.getString("genre"),
                        rs.getInt("stock")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean ajouterLivre(Livre l) throws RemoteException {
        try {
            PreparedStatement check = con.prepareStatement(
                    "SELECT id, stock FROM livre WHERE titre = ? AND auteur = ?");
            check.setString(1, l.getTitre());
            check.setString(2, l.getAuteur());
            ResultSet rs = check.executeQuery();

            if (rs.next()) {

                int idExistant     = rs.getInt("id");
                int stockExistant  = rs.getInt("stock");
                int nouveauStock   = stockExistant + l.getStock();

                PreparedStatement upd = con.prepareStatement(
                        "UPDATE livre SET stock = ? WHERE id = ?");
                upd.setInt(1, nouveauStock);
                upd.setInt(2, idExistant);
                return upd.executeUpdate() > 0;

            } else {

                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO livre (titre, auteur, genre, stock) VALUES (?,?,?,?)");
                ps.setString(1, l.getTitre());
                ps.setString(2, l.getAuteur());
                ps.setString(3, l.getGenre());
                ps.setInt(4, l.getStock());
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.out.println(" Erreur ajouterLivre: " + e.getMessage());
            return false;
        }
    }
    @Override
    public boolean modifierStock(int idLivre, int nouveauStock) throws RemoteException {
        try {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE livre SET stock = ? WHERE id = ?");
            ps.setInt(1, nouveauStock);
            ps.setInt(2, idLivre);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(" Erreur modifierStock: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean supprimerLivre(int id) throws RemoteException {
        try {
            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM livre WHERE id=?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    @Override
    public Membre login(String email, String password) throws RemoteException, SQLException {
        if (con == null) {
            System.out.println(" Connexion BD null !");
            return null;
        }
        try {
        PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM membre WHERE email=? AND password=?");
        ps.setString(1, email);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Membre(
                    rs.getInt("id"), rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
            );
        }
        return null;
    } catch (Exception e) {
            throw new RuntimeException(e);
        }}

    @Override
    public ArrayList<Membre> getMembres() throws RemoteException {
        ArrayList<Membre> list = new ArrayList<>();
        try {
            ResultSet rs = con.createStatement()
                    .executeQuery("SELECT * FROM membre");
            while (rs.next()) {
                list.add(new Membre(rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean ajouterMembre(Membre m) throws RemoteException {
        String sql = "INSERT INTO membre (nom, prenom, email,password,role) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, m.getNom());
            ps.setString(2, m.getPrenom());
            ps.setString(3, m.getEmail());
            ps.setString(4, m.getpassword());
            ps.setString(5, m.getRole());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    @Override
    public boolean emprunterLivre(int idLivre, int idMembre) throws RemoteException {
        try {
            // Vérifier stock disponible
            PreparedStatement check = con.prepareStatement(
                    "SELECT stock FROM livre WHERE id=?");
            check.setInt(1, idLivre);
            ResultSet rs = check.executeQuery();
            if (rs.next() && rs.getInt("stock") <= 0) return false;

            // Créer l'emprunt
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO emprunt (id_livre, id_membre, date_emprunt, date_retour, rendu) " +
                            "VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY), FALSE)");
            ps.setInt(1, idLivre); ps.setInt(2, idMembre);
            ps.executeUpdate();

            // Décrémenter le stock
            PreparedStatement upd = con.prepareStatement(
                    "UPDATE livre SET stock = stock - 1 WHERE id=?");
            upd.setInt(1, idLivre);
            upd.executeUpdate();
            return true;

        } catch (SQLException e) { return false; }
    }

    @Override
    public boolean rendreLivre(int idEmprunt) throws RemoteException {
        try {
            // Marquer comme rendu
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE emprunt SET rendu=TRUE WHERE id=?");
            ps.setInt(1, idEmprunt);
            ps.executeUpdate();

            // Récupérer id_livre pour incrémenter stock
            PreparedStatement getLivre = con.prepareStatement(
                    "SELECT id_livre FROM emprunt WHERE id=?");
            getLivre.setInt(1, idEmprunt);
            ResultSet rs = getLivre.executeQuery();
            if (rs.next()) {
                PreparedStatement upd = con.prepareStatement(
                        "UPDATE livre SET stock = stock + 1 WHERE id=?");
                upd.setInt(1, rs.getInt("id_livre"));
                upd.executeUpdate();
            }
            return true;
        } catch (SQLException e) { return false; }
    }

    @Override
    public ArrayList<Emprunt> getEmprunts() throws RemoteException {
        return getEmpruntsQuery(
                "SELECT e.*, l.titre, CONCAT(m.nom,' ',m.prenom) as membre " +
                        "FROM emprunt e " +
                        "JOIN livre l ON e.id_livre=l.id " +
                        "JOIN membre m ON e.id_membre=m.id " +
                        "WHERE e.rendu=FALSE"
        );
    }

    @Override
    public ArrayList<Emprunt> getEmpruntsParMembre(int idMembre) throws RemoteException {
        return getEmpruntsQuery(
                "SELECT e.*, l.titre, CONCAT(m.nom,' ',m.prenom) as membre " +
                        "FROM emprunt e " +
                        "JOIN livre l ON e.id_livre=l.id " +
                        "JOIN membre m ON e.id_membre=m.id " +
                        "WHERE e.id_membre=" + idMembre
        );
    }

    private ArrayList<Emprunt> getEmpruntsQuery(String sql) {
        ArrayList<Emprunt> list = new ArrayList<>();
        try {
            ResultSet rs = con.createStatement().executeQuery(sql);
            while (rs.next()) {
                list.add(new Emprunt(
                        rs.getInt("id"), rs.getInt("id_livre"),
                        rs.getInt("id_membre"),
                        rs.getDate("date_emprunt"), rs.getDate("date_retour"),
                        rs.getBoolean("rendu"),
                        rs.getString("titre"), rs.getString("membre")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }



    // Attributs partie chat
    ArrayList<String> listMessages       = new ArrayList<>();
    ArrayList<String> listMessagesPrives = new ArrayList<>();

    @Override
    public void envoyerMessage(String idSender, String message) throws RemoteException {
        String msg = "[" + idSender + "] " + message
                + " (" + new java.text.SimpleDateFormat("HH:mm:ss").format(new Date()) + ")";
        listMessages.add(msg);
        System.out.println(msg);
    }

    @Override
    public void envoyerMessagePrive(String idSender, String idReceiver,
                                    String message) throws RemoteException {
        String msg = "[PRIVÉ] " + idSender + " → " + idReceiver
                + " : " + message
                + " (" + new java.text.SimpleDateFormat("HH:mm:ss").format(new Date()) + ")";
        listMessagesPrives.add(msg);
        System.out.println(msg);
    }

    @Override
    public ArrayList<String> getMessages() throws RemoteException {
        return listMessages;
    }

    @Override
    public ArrayList<String> getMessagesPour(String idReceiver) throws RemoteException {
        ArrayList<String> result = new ArrayList<>();
        for (String msg : listMessagesPrives) {
            if (msg.contains("→ " + idReceiver) || msg.contains("[PRIVÉ] " + idReceiver)) {
                result.add(msg);
            }
        }
        return result;
    }
}