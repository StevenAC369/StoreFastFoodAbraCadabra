/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import Entidad.Usuario;
import java.time.LocalDateTime;
/**
 *
 * @author Steven
 */
public class UserDAO {

    public UserDAO() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS usuarios "
                + "(username VARCHAR(10) PRIMARY KEY, password VARCHAR(255), rol VARCHAR(15), fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement createTable = conexion.prepareStatement(createTableSql)) {
            createTable.executeUpdate();

            String insertAdminSql = "INSERT INTO usuarios (username, password, rol) "
                    + "SELECT ?, ?, ? WHERE NOT EXISTS "
                    + "(SELECT 1 FROM usuarios WHERE username = ?)";
            try (PreparedStatement insertAdmin = conexion.prepareStatement(insertAdminSql)) {
                insertAdmin.setString(1, "admin");
                insertAdmin.setString(2, "admin");
                insertAdmin.setString(3, "Propietario");
                insertAdmin.setString(4, "admin");
                if (insertAdmin.executeUpdate() > 0) {
                    System.out.println("Se ha creado el usuario administrador");
                }
            }
        } catch (SQLException ex) {
            System.out.println("No se pudo inicializar la tabla o el usuario: " + ex.getMessage());
        }
    }

    public boolean verificar_usuario(String user, String password) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, password);
            try (ResultSet resultado = ps.executeQuery()) {
                return resultado.next();
            }
        } catch (SQLException ex) {
            System.out.println("No se pudo verificar el usuario: " + ex.getMessage());
            throw ex;
        }
    }
    
    public List<Usuario> ObtenerInformacionUsuarios() throws SQLException
    {
        List<Usuario> users = new ArrayList<>();
        String sql = "SELECT username, rol, fecha_creacion FROM usuarios";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            try (ResultSet resultado = ps.executeQuery()) {
                while (resultado.next()){
                    String username = resultado.getString("username");
                    String rol = resultado.getString("rol");
                    LocalDateTime fecha_creacion = resultado.getObject("fecha_creacion", LocalDateTime.class);
                    Usuario user = new Usuario(username, rol, fecha_creacion);
                    users.add(user);
                }
                return users;
            }
        } catch (SQLException ex) {
            System.out.println("No se pudo obtener la información de usuarios: " + ex.getMessage());
            throw ex;
        }
    }
}
