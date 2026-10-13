/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projects.abracadabra.mavenproject2.Vista.CRUD_Interface;
import Servicios.*;
import javax.swing.JToggleButton;

/**
 *
 * @author Steven
 */
public class AdministradorDePermisos {
    
    public static void VerificarBotonesSuperSudo(JToggleButton... boton)
    {
        if (UsuarioActivo.rol=="Administrador" || UsuarioActivo.rol=="Propietario")
        {
           for (JToggleButton boton_get : boton)
           {
               boton_get.enable(true);
           }
        } else 
        {
           for (JToggleButton boton_get : boton)
           {
               boton_get.enable(false);
           }        
        }   
        
    }
}
