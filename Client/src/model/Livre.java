// Livre.java
package model;
import java.io.Serializable;

public class Livre implements Serializable {
    private int id;
    private String titre, auteur, genre;
    private int stock;

    public Livre(int id, String titre, String auteur, String genre, int stock) {
        this.id = id; this.titre = titre;
        this.auteur = auteur; this.genre = genre;
        this.stock = stock;
    }
    public int getId()       { return id; }
    public String getTitre() { return titre; }
    public String getAuteur(){ return auteur; }
    public String getGenre() { return genre; }
    public int getStock()    { return stock; }
}