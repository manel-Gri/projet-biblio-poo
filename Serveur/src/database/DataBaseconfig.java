package database;

public class DataBaseconfig {
    public static final String NOM_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String IPServeur = "localhost";
    public static final String Port = "3306";
    public static final String DataBaseName = "project";
    public static final String URL_DB  = "jdbc:mysql://"+IPServeur +":"+Port+"/"+DataBaseName;
    public static final String USERNAME = "root";
    public static final String PASSWORD = "";
}
