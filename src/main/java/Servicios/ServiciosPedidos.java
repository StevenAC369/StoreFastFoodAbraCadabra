/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servicios;
import Data.PedidosDAO;
import Entidad.Cliente;
import Entidad.Pedidos;
import Entidad.Producto;
import java.sql.SQLException;
import java.util.List;


/**
 *
 * @author Steven
 */
public class ServiciosPedidos {
    static PedidosDAO pedidos_database = new PedidosDAO();
    
    
    public static void AgregarPedido(List<Producto> productos_a_agregar, Cliente cliente_a_agregar) throws SQLException {
        Pedidos pedido = new Pedidos();
        pedido.setCliente(cliente_a_agregar);
        pedido.setProductos(productos_a_agregar);
        pedidos_database.AgregarPedido(pedido);
    }

    public static int MarcarPedidosPagados(String identificacionCliente) throws SQLException {
        return pedidos_database.MarcarPedidosPagados(identificacionCliente);
    }
    
    public static List<Pedidos> MostrarPedidos()
    {
        return pedidos_database.MostrarPedidos();
    }
    
}
