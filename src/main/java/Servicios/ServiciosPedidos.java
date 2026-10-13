/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servicios;
import Data.*;
import Entidad.*;
import java.util.*;



/**
 *
 * @author Steven
 */
public class ServiciosPedidos {
    static PedidosDAO pedidos_database = new PedidosDAO();
    static ProductosDAO productos_database = new ProductosDAO();
    
    
    public static void AgregarPedido(List<Producto> productos_a_agregar, Cliente cliente_a_agregar, Integer cantidad_antigua)
    {
        try
        {
            Pedidos pedido = new Pedidos();
            pedido.setCliente(cliente_a_agregar);
            pedido.setProductos(productos_a_agregar);
            pedidos_database.AgregarPedido(pedido);
            for (Producto producto : productos_a_agregar) {
                producto.setCantidad(cantidad_antigua - producto.getCantidad());
                producto.setSub(producto.getPrecio() * producto.getCantidad());
                ServiciosProductos.ActualizarProducto(producto.getCodigo(), producto);
            }
            
        }
        
        catch (Exception ex)
        {
           System.out.println("Error: "+ex.getMessage());
        }
        
    }
    
    public static List<Pedidos> MostrarPedidos()
    {
        return pedidos_database.MostrarPedidos();
    }
    
}
