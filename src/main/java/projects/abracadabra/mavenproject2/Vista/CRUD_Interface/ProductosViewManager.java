/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projects.abracadabra.mavenproject2.Vista.CRUD_Interface;

import Servicios.ServiciosProductos;

import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import Entidad.Producto;


/**
 *
 * @author Steven
 */
public class ProductosViewManager {
    
    public static String codigo_antiguo;
    public static Producto producto_antiguo;
    public static void CargarTabla(JTable tabla)
    {
        try
        {
            
            String[] columnas = {"Código", "Descripción", "Precio", "Cantidad", "Estado", "Categoría"};
            DefaultTableModel modelo_tabla = new DefaultTableModel(columnas, 0);
   
                for (Producto producto_obtenido : ServiciosProductos.CargarTabla())
                {
                modelo_tabla.addRow(new Object[]{producto_obtenido.getCodigo(), producto_obtenido.getDescripcion(), producto_obtenido.getPrecio(), producto_obtenido.getCantidad(), producto_obtenido.getEstado(), producto_obtenido.getCategoria()});
            }
    
            tabla.setModel(modelo_tabla);
        }
        catch (Exception ex)
        {
             JOptionPane.showMessageDialog(null, "No se pudo cargar la tabla Productos", "Vista", JOptionPane.ERROR_MESSAGE);
        }
    }
    static public void AgregarProducto(String codigo, String descripcion, String precio, String cantidad, Object Estado, Object Categoria)
    {
        try
        {
            String estado = String.valueOf(Estado);
            String categoria = String.valueOf(Categoria);
            Producto producto_a_agregar = new Producto(codigo, descripcion, Double.valueOf(precio), Integer.valueOf(cantidad), estado, categoria);
            ServiciosProductos.AgregarProducto(producto_a_agregar);
        }
        catch (NumberFormatException ex)
        {
            JOptionPane.showMessageDialog(null, "No se pudo agregar el producto", "Vista", JOptionPane.ERROR_MESSAGE);
        }
            
    }

    public static void EliminarProducto(JTable tabla)
    {
        try 
        {
            int FilaSeleccionada = tabla.getSelectedRow();
            if (FilaSeleccionada!=-1) 
            {
                String codigo = tabla.getValueAt(FilaSeleccionada, 0).toString();
                String descripcion = tabla.getValueAt(FilaSeleccionada, 1).toString();
                Double precio = Double.valueOf(tabla.getValueAt(FilaSeleccionada, 2).toString());
                Integer cantidad = Integer.valueOf(tabla.getValueAt(FilaSeleccionada, 3).toString());
                String estado = tabla.getValueAt(FilaSeleccionada, 4).toString();
                String categoria = tabla.getValueAt(FilaSeleccionada, 5).toString();
                Producto producto = new Producto(codigo, descripcion, precio, cantidad, estado, categoria);
                ServiciosProductos.EliminarProducto(producto);
               
            } 
        }
        catch (Exception ex)
        {
             JOptionPane.showMessageDialog(null, "No se pudo Eliminar el producto: "+ex.toString(), "Servicios", JOptionPane.ERROR_MESSAGE);   
        }
    }

    static public void BuscarProducto(String codigo, JTable table, String parametro1, String parametro2, Boolean SIMILAR_TO)
    {
        try
        {
            if(codigo==null)
            {
                // Actualiza tabla si el codigo ingresado es null o está vacío
                CargarTabla(table);
                return;
            }
            
            String[] columnas = {"Código", "Descripción", "Precio", "Cantidad", "Estado", "Categoría"};
            DefaultTableModel modelo_tabla = new DefaultTableModel(columnas, 0);
   
            for (Producto producto_obtenido : ServiciosProductos.BuscarProducto(codigo, parametro1, parametro2, SIMILAR_TO))
            {
                modelo_tabla.addRow(new Object[]{producto_obtenido.getCodigo(), producto_obtenido.getDescripcion(), producto_obtenido.getPrecio(), producto_obtenido.getCantidad(), producto_obtenido.getEstado(), producto_obtenido.getCategoria()});
            }
    
            table.setModel(modelo_tabla);
        }
       finally
        {
            
        }
        
    }
    
    public static void EditarProducto(String codigo, String descripcion, String precio, String cantidad, String estado, String Categoria)
    {
        try
        {
            Producto product = new Producto(codigo, descripcion, Double.parseDouble(precio), Integer.parseInt(cantidad), estado,Categoria);
            ServiciosProductos.ActualizarProducto(producto_antiguo.getCodigo(), product);
        }
        catch (Exception ex)
        {
            System.out.println("Error: "+ex.toString());
        }
    }
    
       
    public static void CargarProductoAEditar(JTable tabla)
    {
        try
        {
            int FilaSeleccionada = tabla.getSelectedRow();
            if (FilaSeleccionada!=-1) 
            {
                String codigo = tabla.getValueAt(FilaSeleccionada, 0).toString();
                String descripcion = tabla.getValueAt(FilaSeleccionada, 1).toString();
                Double precio = Double.valueOf(tabla.getValueAt(FilaSeleccionada, 2).toString());
                Integer cantidad = Integer.valueOf(tabla.getValueAt(FilaSeleccionada, 3).toString());
                String estado = tabla.getValueAt(FilaSeleccionada, 4).toString();
                String categoria = tabla.getValueAt(FilaSeleccionada, 5).toString();
                Producto producto = new Producto(codigo, descripcion, precio, cantidad, estado, categoria);
                producto_antiguo = producto;
               
            } else {
                JOptionPane.showMessageDialog(null, "No se ha seleccionado ningún producto para editar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        catch (Exception ex)
        {
            System.out.println("Error: "+ex.toString());
                    
        }
    }
    
    public static void CargarCampos(JTextPane codigo, JTextPane descripcion, JTextPane precio, JTextPane cantidad, JComboBox estado, JComboBox categoria)
    {
        codigo.setText(producto_antiguo.getCodigo());
        descripcion.setText(producto_antiguo.getDescripcion());
        precio.setText(String.valueOf(producto_antiguo.getPrecio()));
        cantidad.setText(String.valueOf(producto_antiguo.getCantidad()));
        estado.setSelectedItem(producto_antiguo.getEstado());
        categoria.setSelectedItem(producto_antiguo.getCategoria());
    }    
    
    public static void CalcularTotal()
    {
        
    }
        
}
