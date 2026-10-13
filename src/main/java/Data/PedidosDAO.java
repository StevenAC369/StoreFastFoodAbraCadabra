/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Data;

import Entidad.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import javax.swing.JOptionPane;
import java.util.*;
import java.time.LocalTime;
/**
 *
 * @author Steven
 */
public class PedidosDAO {
    public PedidosDAO()
    {
        String sql = "CREATE TABLE IF NOT EXISTS pedidos(id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, cliente_id VARCHAR(255) UNIQUE REFERENCES clientes(identificacion) ON DELETE CASCADE, fecha_creacion DATE DEFAULT CURRENT_DATE, hora_creacion TIME DEFAULT CURRENT_TIME)";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (ps.executeUpdate() > 0) {
                    System.out.println("Se ha creado la tabla de pedidos");
                    ps.close();
            }
        } catch (SQLException ex) {
            System.out.println("No se pudo crear la tabla de pedidos:"+ex.toString());
            
        }
        sql = "CREATE TABLE IF NOT EXISTS pedidos_item (pedido_id INTEGER NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE, producto_id VARCHAR(255) NOT NULL REFERENCES productos(codigo), cantidad INTEGER NOT NULL, precio_unitario NUMERIC(10,2) NOT NULL, subtotal NUMERIC(10,2) GENERATED ALWAYS AS (cantidad * precio_unitario) STORED, PRIMARY KEY (pedido_id, producto_id))";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (ps.executeUpdate() > 0) {
                    System.out.println("Se ha creado la tabla de pedidos");
                    ps.close();
            }
        } catch (SQLException ex) {
            System.out.println("No se pudo crear la tabla de pedidos:"+ex.toString());
            
        }
    }

    public void AgregarPedido(Pedidos pedido) throws SQLException 
    {
        System.out.println("ID del pedido: " + obtenerIDpedido(pedido.getCliente().getIdentificacion()));
        String sql = """
                     INSERT INTO pedidos (cliente_id)
                     VALUES (?)
                     ON CONFLICT (cliente_id)
                     DO NOTHING
                     """;
        try 
            (Connection conexion = Conexion.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql))
            {
                ps.setString(1, pedido.getCliente().getIdentificacion());
                ps.executeUpdate(); 
            }
            catch (SQLException ex) {
            System.out.println("No se pudo guardar el pedido:"+ex.toString());
        };
        pedido.setID(obtenerIDpedido(pedido.getCliente().getIdentificacion()));
        ActualizarPedidoItem(pedido);
    }
    
    public static void ActualizarPedidoItem(Pedidos pedido)
    {
        String sql = """
                    INSERT INTO pedidos_item (pedido_id, producto_id, cantidad, precio_unitario)
                    VALUES (?, ?, ?, ?)
                    ON CONFLICT (pedido_id, producto_id)
                    DO UPDATE SET 
                        cantidad = pedidos_item.cantidad + EXCLUDED.cantidad
                     """;
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

                for (Producto producto : pedido.getProductos()) {
                    ps.setInt(1, pedido.getID());
                    ps.setString(2, producto.getCodigo());
                    ps.setInt(3, producto.getCantidad());
                    ps.setDouble(4, producto.getPrecio());
                    int update = ps.executeUpdate();
                    if (update > 0) {
                        System.out.println("Se ha actualizado el pedido correctamente");
                    } else {
                        System.out.println("No se pudo actualizar el pedido");
                    }
                }
            System.out.println("Se ha guardado correctamente");
        } catch (SQLException ex) {
            System.out.println("Database - Error al guardar el pedido: " + ex.getMessage());
        }
    }
    
    public void EliminarPedido(Pedidos pedido)
    {
        String sql = "DELETE FROM pedidos WHERE id=?";
        try (Connection conexion = Conexion.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, pedido.getID());
            int update = ps.executeUpdate();
            if (update >0)
            {
                JOptionPane.showMessageDialog(null, "Se ha eliminado correctamente", "Database", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
             System.out.println("Database - Error al eliminar el pedido: " + ex.getMessage());
        }
    }
    
    /*public Boolean verificarPedido(Pedidos pedido)
    {
        String sql = "SELECT cliente_id FROM pedidos WHERE=?";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, pedido.getCliente().getIdentificacion());
            return ps.executeUpdate()>1;
            
        } catch (SQLException ex) {
            System.out.println("Database - Error al verificar el pedido: " + ex.getMessage());
        }
        return false;
        
        
    } */

    public List<Pedidos> MostrarPedidos()
    {
        List<Pedidos> pedidos = new ArrayList<>();
        
        String sql = """
            SELECT p.id, c.identificacion, c.nombres, c.telefono, c.direccion, i.producto_id, k.descripcion, k.categoria, k.estado, k.precio, i.cantidad, i.subtotal, p.fecha_creacion, p.hora_creacion
            FROM pedidos p
            INNER JOIN clientes c ON c.identificacion = p.cliente_id
            INNER JOIN pedidos_item i ON p.id = i.pedido_id
            INNER JOIN productos k ON k.codigo = i.producto_id
            """;
        try (Connection conexion = Conexion.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            String id_pedido_anterior = null;
            while (rs.next()) {
                Time hora = rs.getTime("hora_creacion");
                LocalTime horaLocal = hora != null ? hora.toLocalTime() : null;
                if (( rs.getString("id")) == null ? id_pedido_anterior == null : ( rs.getString("id")).equals(id_pedido_anterior))
                {
                    Producto producto = new Producto(rs.getString("producto_id"), rs.getString("descripcion"), rs.getDouble("precio"),rs.getInt("cantidad"), rs.getString("estado"), rs.getString("categoria"));
                    producto.setSub(rs.getDouble("subtotal"));
                    pedidos.get(pedidos.size() - 1).AddProducto(producto);
                    
                    continue;
                }
                id_pedido_anterior = rs.getString("id");
                Cliente cliente = new Cliente(
                    rs.getString("identificacion"),
                    rs.getString("nombres"),
                    rs.getString("telefono"),
                    rs.getString("direccion")
                );
                
                pedidos.add(new Pedidos(
                    rs.getInt("id"),
                    cliente,
                    rs.getDate("fecha_creacion"),
                    horaLocal
                ));
                    pedidos.get(pedidos.size() - 1).AddProducto(new Producto(rs.getString("producto_id"), rs.getString("descripcion"), rs.getDouble("subtotal"),rs.getInt("cantidad"), rs.getString("estado"), rs.getString("categoria")));
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al listar los pedidos: " + ex.getMessage(),
                    "Database", JOptionPane.ERROR_MESSAGE);
        }
        return pedidos;
    }
    
    
    public static Integer obtenerIDpedido(String identificacion_user)
    {
        String sql = "SELECT id FROM pedidos WHERE cliente_id=?";
        try (Connection conexion = Conexion.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql))
            {
                ps.setString(1, identificacion_user);
                ResultSet rs= ps.executeQuery();
                while (rs.next()){
                    if (rs.getInt("id") != 0)
                    {
                        System.out.println("ID del pedido: " + rs.getInt("id"));
                        return rs.getInt("id");
                    }    
                }
                return null;
            }
        catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, "Error al guardar", "Database", JOptionPane.ERROR_MESSAGE);
             return null;
        }
        
    }
    
}
