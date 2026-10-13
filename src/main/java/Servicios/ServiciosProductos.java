/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servicios;
import Data.ProductosDAO;
import Entidad.Producto;
import java.util.*;




/**
 *
 * @author Steven
 */
public class ServiciosProductos {
    
    static ProductosDAO database_productos = new ProductosDAO();
    
    public static List<Producto> CargarTabla()
    {
        return database_productos.ListarProductos();
        
    }
    
    static public void AgregarProducto(Producto producto_a_agregar)
    {
        database_productos.AgregarProducto(producto_a_agregar);
            
    }
    
    static public void EliminarProducto(Producto producto_a_eliminar)
    {
        database_productos.EliminarProducto(producto_a_eliminar);
    }
    
    static public List<Producto> BuscarProducto(String codigo, String parametro1, String parametro2, Boolean SIMILAR_TO)
    {
        return database_productos.BuscarProducto(codigo, parametro1, parametro2, SIMILAR_TO); 
    }
    
    static public void ActualizarProducto(String codigo, Producto producto_a_actualizar)
    {
        database_productos.ActualizarProducto(codigo, producto_a_actualizar);
    }

}
