/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servicios;

import Data.ClienteDAO;
import Entidad.Cliente;
import java.util.List;
import javax.swing.JTable;




/**
 *
 * @author Steven
 */
public class ServiciosCliente {

    private static final ClienteDAO databaseClientes = new ClienteDAO();
    
    public static Cliente cliente_a_agregar;
    
    public static Boolean IsClient;


    public static List<Cliente> MostrarClientes(JTable tabla) {
        
        return databaseClientes.ListarClientes();
        
    }

    public static void AgregarCliente(Cliente cliente) {
        databaseClientes.AgregarCliente(cliente);
    }

    public static void EliminarCliente(Cliente cliente) {
        databaseClientes.EliminarCliente(cliente);
    }

    public static List<Cliente> BuscarCliente(String identificacion, String parametro1, String parametro2, boolean isSearch) 
    {
        return databaseClientes.BuscarCliente(identificacion, parametro1, parametro2, isSearch); 
    }
    public static void ActualizarCliente(String id_antiguo, Cliente cliente) {
        databaseClientes.ActualizarCliente(id_antiguo, cliente);
    }
    
    
    
    
    
}
