/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Steven
 */
public class Conexion {
    private static final String URL = "jdbc:postgresql://localhost/tienda";
    private static final String USUARIO = "yumi";
    private static final String PASSWORD = "gnfghngfbd"; 
    
    

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}