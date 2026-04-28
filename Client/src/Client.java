import IHM.LoginFrame;
import rmi.BibliRemote;
import java.rmi.Naming;
import javax.swing.SwingUtilities;

public class Client {
    public static void main(String[] args) {
        try {
            BibliRemote remote = (BibliRemote)
                    Naming.lookup("rmi://127.0.0.1:9005/bibli");

            SwingUtilities.invokeLater(() ->
                    new LoginFrame(remote).setVisible(true));

        } catch (Exception e) {
            System.out.println(" Erreur connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}