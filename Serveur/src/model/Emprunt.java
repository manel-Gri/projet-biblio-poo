// Emprunt.java
package model;
import java.io.Serializable;
import java.util.Date;

public class Emprunt implements Serializable {
    private int id, idLivre, idMembre;
    private Date dateEmprunt, dateRetour;
    private boolean rendu;
    private String titreLivre, nomMembre;

    public Emprunt(int id, int idLivre, int idMembre,
                   Date dateEmprunt, Date dateRetour,
                   boolean rendu, String titreLivre, String nomMembre) {
        this.id = id; this.idLivre = idLivre;
        this.idMembre = idMembre; this.dateEmprunt = dateEmprunt;
        this.dateRetour = dateRetour; this.rendu = rendu;
        this.titreLivre = titreLivre; this.nomMembre = nomMembre;
    }
    public int getId()            { return id; }
    public int getIdLivre()       { return idLivre; }
    public int getIdMembre()      { return idMembre; }
    public Date getDateEmprunt()  { return dateEmprunt; }
    public Date getDateRetour()   { return dateRetour; }
    public boolean isRendu()      { return rendu; }
    public String getTitreLivre() { return titreLivre; }
    public String getNomMembre()  { return nomMembre; }
}