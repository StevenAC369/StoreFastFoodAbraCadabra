/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servicios;
import Data.UserDAO;
import java.sql.SQLException;
import Entidad.Usuario;

import java.util.*;


/**
 *
 * @author Steven
 */
public class ServiciosUsuario {
    static UserDAO database = new UserDAO();
    
    static public boolean Ingresar(String user, String password) throws SQLException {
        return database.verificar_usuario(user, password);
    }
    
    static public Boolean VerificarPermisosSuperSudo()
    {
        if(ClienteActivo.rol.equals("Administrador")|| ClienteActivo.rol.equals("Propietario")|| ClienteActivo.rol.equals("Empleado"))
        {
            return true;
        }
        return false;
        
    }
    
    
     static public List<Usuario> MostrarUsuarios() throws SQLException
    {
        return database.ObtenerInformacionUsuarios();
    }
    
}
