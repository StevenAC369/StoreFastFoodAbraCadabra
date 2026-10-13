/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projects.abracadabra.mavenproject2.Vista.CRUD_Interface;
import java.util.*;
import Servicios.ServiciosCliente;
import Entidad.Cliente;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Steven
 */
public class ClienteViewManager {
    public static void CargarTabla(JTable tabla) {
        try
        {
            List<Cliente> clientes = ServiciosCliente.MostrarClientes(tabla);
            DefaultTableModel modeloTabla = new DefaultTableModel(
                    new String[]{"identificacion", "nombres", "telefono", "direccion"}, 0);
            for (Cliente cliente : clientes) {
                modeloTabla.addRow(new Object[]{
                    cliente.getIdentificacion(),
                    cliente.getNombres(),
                    cliente.getTelefono(),
                    cliente.getDireccion()
                });
            }
            tabla.setModel(modeloTabla);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "No se pudo cargar la tabla Clientes: "
                    + ex.getMessage(), "Servicios", JOptionPane.ERROR_MESSAGE);
        } 
    }

    public static void AgregarCliente(String identificacion, String nombres, String telefono, String direccion) {
        if(identificacion == null || identificacion.isEmpty() ||
           nombres == null || nombres.isEmpty() ||
           telefono == null || telefono.isEmpty() ||
           direccion == null || direccion.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios",
                    "Servicios", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Cliente cliente = new Cliente(identificacion, nombres, telefono, direccion);
        ServiciosCliente.AgregarCliente(cliente);
    }

    public static void EliminarCliente(JTable tablaClientes) {
        int filaSeleccionada = tablaClientes.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un cliente para eliminar",
                    "Servicios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object identificacion = tablaClientes.getValueAt(filaSeleccionada, 0);
        Object nombres = tablaClientes.getValueAt(filaSeleccionada, 1);
        Object telefono = tablaClientes.getValueAt(filaSeleccionada, 2);
        Object direccion = tablaClientes.getValueAt(filaSeleccionada, 3);
        
        
        if (identificacion == null) {
            JOptionPane.showMessageDialog(null, "La fila seleccionada no tiene identificación",
                    "Servicios", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Cliente cliente = new Cliente(identificacion.toString(), nombres.toString(), telefono.toString(), direccion.toString());
        ServiciosCliente.EliminarCliente(cliente);
    }
    
    
    public static void BuscarCliente(JTextField textField_identificacion, KeyEvent e, JTable tabla, String parametro1, String parametro2) 
    {
        
        try {
            String identificacion = textField_identificacion.getText();
            
            
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) 
                    {
                        List<Cliente> clientes = ServiciosCliente.BuscarCliente(identificacion, parametro1, parametro2, true);
                        DefaultTableModel modeloTabla = new DefaultTableModel(
                        new String[]{"identificacion", "nombres", "telefono", "direccion"}, 0);
                            for (Cliente cliente : clientes) {
                                modeloTabla.addRow(new Object[]{
                                cliente.getIdentificacion(),
                                cliente.getNombres(),
                                cliente.getTelefono(),
                                cliente.getDireccion()
                                });
                            }
                        tabla.setModel(modeloTabla);
                    }
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) 
                    {
                        textField_identificacion.setText("");
                        CargarTabla(tabla);
                    }
                
                }
    
                    
        catch (Exception ex) 
        {
             JOptionPane.showMessageDialog(null, "No se pudo buscar el cliente: "
                    + ex.getMessage(), "Servicios", JOptionPane.ERROR_MESSAGE);
        }
    
    }
    public static void ActualizarClient(JTable tablaClientes, String Identificacion_nueva, String Nombres_nuevos, String Telefono_nuevo, String Correo_nuevo) {
        int filaSeleccionada = tablaClientes.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un cliente para Actualizar",
                    "Servicios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object identificacion = tablaClientes.getValueAt(filaSeleccionada, 0);
        if (identificacion == null) {
            JOptionPane.showMessageDialog(null, "La fila seleccionada no tiene identificación",
                    "Servicios", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ServiciosCliente.ActualizarCliente(identificacion.toString(), new Cliente(Identificacion_nueva, Nombres_nuevos, Telefono_nuevo, Correo_nuevo));
    }
    public static void SeleccionarCliente(JTable tabla)
    {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un cliente para Actualizar",
                    "Servicios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object identificacion = tabla.getValueAt(filaSeleccionada, 0);
        
        
        
        if (identificacion == null) {
            JOptionPane.showMessageDialog(null, "La fila seleccionada no tiene identificación",
                    "Servicios", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Object nombres = tabla.getValueAt(filaSeleccionada, 1);
        Object telefono = tabla.getValueAt(filaSeleccionada, 2);
        Object direccion = tabla.getValueAt(filaSeleccionada, 3);
        
        Cliente cliente = new Cliente(identificacion.toString(), nombres.toString(), telefono.toString(), direccion.toString());
        ServiciosCliente.cliente_a_agregar = cliente;
        JOptionPane.showMessageDialog(null, "Se ha seleccionado "+nombres.toString(),
                    "Servicios", JOptionPane.INFORMATION_MESSAGE);
        ServiciosCliente.IsClient=true;
    }

    public static void CargarDatosCliente(JTextField identificacion,JTextField nombres, JTextField direccion)
    {
      
        if (ServiciosCliente.cliente_a_agregar!=null && ServiciosCliente.IsClient)
        {
            identificacion.enable(true);
            nombres.enable(true);
            direccion.enable(true);
        
            identificacion.setText(ServiciosCliente.cliente_a_agregar.getIdentificacion());
            nombres.setText(ServiciosCliente.cliente_a_agregar.getNombres());
            direccion.setText(ServiciosCliente.cliente_a_agregar.getDireccion());
        
            identificacion.enable(false);
            nombres.enable(false);
            direccion.enable(false);
            return;
            
        }
        ServiciosCliente.cliente_a_agregar = null;
        
        if (ServiciosCliente.IsClient==false)
        {
            identificacion.setText("999999999");
            nombres.setText("Consumidor Final");
            direccion.setText("9999999999");
            Cliente client0 = new Cliente("999999999","Consumidor Final", "999999999", "999999999");
            ServiciosCliente.cliente_a_agregar = client0;
        }
        
    }
}
