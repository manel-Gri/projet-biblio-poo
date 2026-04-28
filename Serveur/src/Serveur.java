import rmi.BibliImpl;
import rmi.BibliRemote;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Serveur {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(9005);
            BibliRemote impl = new BibliImpl();
            Naming.rebind("rmi://localhost:9005/bibli", impl);
            System.out.println("Serveur Bibliothèque démarré sur le port 9005");
        } catch (Exception e) {
            System.out.println(" Erreur serveur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}