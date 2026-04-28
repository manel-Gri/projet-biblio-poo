package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection makeConnection(){
        try {
            Class.forName(DataBaseconfig.NOM_DRIVER);
            System.out.println("Driver ok ..");
        } catch (ClassNotFoundException e) {
            System.out.println("Error drive "+e.getMessage());
        }
        Connection con = null;
        try {
            con = DriverManager.getConnection(DataBaseconfig.URL_DB,DataBaseconfig.USERNAME,DataBaseconfig.PASSWORD);
            System.out.println("Connected ..");
        } catch (SQLException e) {
            System.out.println("Error de cnx "+e.getMessage());
        }
        return con;
    }
}
