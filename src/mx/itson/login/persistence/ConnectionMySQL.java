package mx.itson.login.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConnectionMySQL {
    
    private static final String user = "your_dbuser";
    private static final String pass = "your_dbpass";
    private static final String url = "jdbc:mysql://localhost:3306/logindb?characterEncoding=utf8";
    
    public static Connection getConnection(){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, pass);
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.toString());
        }
        return connection;
    }
}
