package rmi;

import model.Livre;
import model.Membre;
import model.Emprunt;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

public interface BibliRemote extends Remote {

    // ── Livres ──
    ArrayList<Livre>   getLivres()                          throws RemoteException;
    ArrayList<Livre>   rechercherLivre(String q)            throws RemoteException;
    boolean            ajouterLivre(Livre l)                throws RemoteException;
    boolean            supprimerLivre(int id)               throws RemoteException;
    boolean modifierStock(int idLivre, int nouveauStock) throws RemoteException;

    // ── Membres ──
    ArrayList<Membre>  getMembres()                         throws RemoteException;
    Membre             login(String email,String password)                  throws RemoteException, SQLException;
    boolean            ajouterMembre(Membre m)              throws RemoteException;


    // ── Emprunts ──
    ArrayList<Emprunt> getEmprunts()                        throws RemoteException;
    ArrayList<Emprunt> getEmpruntsParMembre(int idMembre)   throws RemoteException;
    boolean            emprunterLivre(int idLivre,
                                      int idMembre)         throws RemoteException;
    boolean            rendreLivre(int idEmprunt)           throws RemoteException;

    // ── Chat ──
    void envoyerMessage(String idSender, String message)           throws RemoteException;
    void envoyerMessagePrive(String idSender, String idReceiver,
                             String message)                       throws RemoteException;
    ArrayList<String> getMessages()                                throws RemoteException;
    ArrayList<String> getMessagesPour(String idReceiver)           throws RemoteException;
}