/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projects.abracadabra.mavenproject2.Vista.CRUD_Interface;

import Entidad.Usuario;
import Servicios.ServiciosUsuario;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;


/**
 *
 * @author Steven
 */
public class UsuariosViewManager {
    
    public static void CargarTablaUsuarios(JTable tabla, JLabel label_info)
    {
        try
        {
            int total_usuarios_con_acceso=0;
            
            List<Usuario> usuarios = ServiciosUsuario.MostrarUsuarios();
            DefaultTableModel modeloTabla = new DefaultTableModel(
                    new String[]{"Usuario", "Rol", "Fecha de creación"}, 0);
            for (Usuario usuario : usuarios) {
                
                modeloTabla.addRow(new Object[]{
                    usuario.getUsername(),
                    usuario.getRol(),
                    usuario.getfecha_creacion(),
                });
                if("Administrador".equals(usuario.getRol()) ||"Propietario".equals(usuario.getRol()) )
                {
                    total_usuarios_con_acceso=total_usuarios_con_acceso+1;
                }
            }
            label_info.setText(total_usuarios_con_acceso+" personas tienen acceso a este espacio.");
            
            tabla.setModel(modeloTabla);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "No se pudo cargar la tabla Usuarios: "
                    + ex.getMessage(), "Vista", JOptionPane.ERROR_MESSAGE);
        } 
    }
            
}
