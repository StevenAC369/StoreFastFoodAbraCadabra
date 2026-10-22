/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projects.abracadabra.mavenproject2.Vista.CRUD_Interface;

import java.util.*;

import Servicios.UsuarioActivo;
import javax.swing.*;
import Entidad.Usuario;

/**
 *
 * @author Steven
 */
public class Permisos {

    public static void CargarRoles()
    {
        List<Usuario> userList = new ArrayList<>();
        try
        {
            userList = Servicios.ServiciosUsuario.MostrarUsuarios();
            for (Usuario usuario : userList)
            {
                if (UsuarioActivo.username.equals(usuario.getUsername()))
                {
                    UsuarioActivo.rol = usuario.getRol();
                    return; 
                }
            }
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, "No se pudo cargar la lista de usuarios", "Vista", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void VerificarPermisos(JTable AccionesRecientes, JTextField buscar, JTable Productos, JTable pedidos, JTable ColaDeCocina, Object... botones)
    {
        CargarRoles();
        if ("Visualizador".equals(UsuarioActivo.rol))
        {
            AccionesRecientes.setModel(new javax.swing.table.DefaultTableModel());
            AccionesRecientes.setEnabled(false);

            Productos.setModel(new javax.swing.table.DefaultTableModel());
            Productos.setEnabled(false);
            
            pedidos.setModel(new javax.swing.table.DefaultTableModel());
            pedidos.setEnabled(false);
            
            ColaDeCocina.setModel(new javax.swing.table.DefaultTableModel());
            ColaDeCocina.setEnabled(false);
        }

        if ("Visualizador".equals(UsuarioActivo.rol))
        {
            for (Object btn : botones)
            {
                if (btn instanceof JToggleButton)
                {
                    ((JToggleButton) btn).setEnabled(false);
                }

                if (btn instanceof JButton)
                {
                    ((JButton) btn).setEnabled(false);
                }
                
            }
            buscar.setEnabled(false);
        }
        if ("Cajero".equals(UsuarioActivo.rol))
        {
            for (JToggleButton btn : botones)
            {
                if (btn.getText().equals("Agregar Producto") || btn.getText().equals("Editar Producto") || btn.getText().equals("Eliminar Producto") || btn.getText().equals("Administrar Clientes") || btn.getText().equals("Ajustes"))
                {
                    btn.setEnabled(false);
                }
            }
        }
    }
    
}
