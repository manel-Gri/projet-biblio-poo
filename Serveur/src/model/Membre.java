
package model;
import java.io.Serializable;

public class Membre implements Serializable {
    private int id;
    private String nom, prenom, email,password,role;

    public Membre(int id, String nom, String prenom, String email,String password,String role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password=password;
        this.role=role;
    }
    public int getId()       { return id; }
    public String getNom()   { return nom; }
    public String getPrenom(){ return prenom; }
    public String getEmail() { return email; }
    public String getpassword() { return password; }
    public String getRole() { return role; }
}