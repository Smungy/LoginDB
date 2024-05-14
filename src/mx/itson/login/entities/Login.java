package mx.itson.login.entities;

import mx.itson.login.persistence.ConnectionMySQL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import mx.itson.login.ui.FormAdmin;
import mx.itson.login.ui.FormColaborador;

public class Login {
    
    // Método para hashear una contraseña utilizando SHA-256
    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    //Metodo para registrar un usuario
  public void registrarUsuario(String usuario, String password) {
    ConnectionMySQL connectionMySQL = new ConnectionMySQL();
    String sql;
    if (usuario.equals("admin")) {
        sql = "INSERT INTO administradores(nombre, clave) VALUES (?, ?)";
    } else {
        sql = "INSERT INTO usuarios(nombre, clave) VALUES (?, ?)";
    }
    try (Connection connection = connectionMySQL.getConnection();
         PreparedStatement pst = connection.prepareStatement(sql)) {
        
        // Encriptar la contraseña antes de almacenarla
        String hashedPassword = hashPassword(password);
        
        pst.setString(1, usuario);
        pst.setString(2, hashedPassword);
        int rs = pst.executeUpdate();
        JOptionPane.showMessageDialog(null, "Guardado correctamente");

    } catch(SQLException | NoSuchAlgorithmException e) {
        System.out.println(e);
    }
}
    //Metodo para logear a un usuario
   public void loginUsuario(String user, String pass) {
    ConnectionMySQL connectionMySQL = new ConnectionMySQL();
    try (Connection connection = connectionMySQL.getConnection()) {
        // Consultar la tabla de usuarios
        try (PreparedStatement pst = connection.prepareStatement("SELECT clave FROM usuarios WHERE nombre = ?")) {
            pst.setString(1, user);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String hashedInputPassword = hashPassword(pass); // Hashear la contraseña ingresada
                    String storedPassword = rs.getString(1); // Obtener el valor de la primera columna (clave)
                    if (hashedInputPassword.equals(storedPassword)) { // Comparar hashes
                        // Redirigir al formulario de colaborador
                        FormColaborador colaboradorForm = new FormColaborador();
                        colaboradorForm.setVisible(true);
                        JOptionPane.showMessageDialog(null, "Login correcto. ¡Bienvenido Colaborador!");
                        return; // Salir del método si el inicio de sesión es exitoso
                    }
                }
            }
        }
        
        // Si no se encontró al usuario en la tabla de usuarios, consultar la tabla de administradores
        try (PreparedStatement pstAdmin = connection.prepareStatement("SELECT clave FROM administradores WHERE nombre = ?")) {
            pstAdmin.setString(1, user);
            try (ResultSet rsAdmin = pstAdmin.executeQuery()) {
                if (rsAdmin.next()) {
                    String hashedInputPassword = hashPassword(pass); // Hashear la contraseña ingresada
                    String storedPassword = rsAdmin.getString(1); // Obtener el valor de la primera columna (clave)
                    if (hashedInputPassword.equals(storedPassword)) { // Comparar hashes
                        // Redirigir al formulario de administrador
                        FormAdmin adminForm = new FormAdmin();
                        adminForm.setVisible(true);
                        JOptionPane.showMessageDialog(null, "Login correcto. ¡Bienvenido Administrador!");
                        return; // Salir del método si el inicio de sesión es exitoso
                    }
                }
            }
        }
        
        // Si no se encontró al usuario ni en la tabla de usuarios ni en la tabla de administradores
        JOptionPane.showMessageDialog(null, "Usuario no encontrado o usuario ya existente");
    } catch (SQLException | NoSuchAlgorithmException e) {
        JOptionPane.showMessageDialog(null, "Error al consultar usuario: " + e.getMessage());
    }
}
}
