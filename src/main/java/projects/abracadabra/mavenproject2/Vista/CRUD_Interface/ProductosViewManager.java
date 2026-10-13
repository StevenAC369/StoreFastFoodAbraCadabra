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
    public static void CargarTabla(JTable tabla)
    {
        try
        {
            
            String[] columnas = {"codigo","descripcion", "precio", "cantidad", "estado", "categoria"};
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

    public static void EliminarProducto(JTable JTableProductos)
    {
        try 
        {
            int FilaSeleccionada = JTableProductos.getSelectedRow();
            if (FilaSeleccionada!=-1) 
            {
                String codigo = JTableProductos.getValueAt(FilaSeleccionada, 0).toString();
                String descripcion = JTableProductos.getValueAt(FilaSeleccionada, 1).toString();
                Double precio = Double.valueOf(JTableProductos.getValueAt(FilaSeleccionada, 2).toString());
                Integer cantidad = Integer.valueOf(JTableProductos.getValueAt(FilaSeleccionada, 3).toString());
                String estado = JTableProductos.getValueAt(FilaSeleccionada, 4).toString();
                String categoria = JTableProductos.getValueAt(FilaSeleccionada, 5).toString();
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
            
            String[] columnas = {"codigo","descripcion", "precio", "cantidad", "estado", "categoria"};
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
}
