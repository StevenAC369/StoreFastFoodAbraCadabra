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
import java.sql.Statement;
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
        String sql = "CREATE TABLE IF NOT EXISTS pedidos(id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, cliente_id VARCHAR(255) REFERENCES clientes(identificacion) ON DELETE CASCADE, estado VARCHAR(30) NOT NULL DEFAULT 'Pendiente de pago', fecha_creacion DATE DEFAULT CURRENT_DATE, hora_creacion TIME DEFAULT CURRENT_TIME)";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("No se pudo crear la tabla de pedidos:"+ex.toString());
        }

        sql = "ALTER TABLE pedidos ADD COLUMN IF NOT EXISTS estado VARCHAR(30) NOT NULL DEFAULT 'Pendiente de pago'";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("No se pudo actualizar la tabla de pedidos: " + ex.getMessage());
        }

        sql = """
              DO $$
              DECLARE constraint_record RECORD;
              BEGIN
                  FOR constraint_record IN
                      SELECT DISTINCT constraint_info.conname
                      FROM pg_constraint constraint_info
                      JOIN pg_class table_info ON table_info.oid = constraint_info.conrelid
                      JOIN pg_namespace schema_info ON schema_info.oid = table_info.relnamespace
                      JOIN pg_attribute column_info
                        ON column_info.attrelid = table_info.oid
                       AND column_info.attnum = ANY(constraint_info.conkey)
                      WHERE schema_info.nspname = current_schema()
                        AND table_info.relname = 'pedidos'
                        AND constraint_info.contype = 'u'
                        AND column_info.attname = 'cliente_id'
                  LOOP
                      EXECUTE format('ALTER TABLE pedidos DROP CONSTRAINT %I', constraint_record.conname);
                  END LOOP;
              END $$;
              """;
        try (Connection conexion = Conexion.obtenerConexion();
             Statement statement = conexion.createStatement()) {
            statement.execute(sql);
        } catch (SQLException ex) {
            System.out.println("No se pudo habilitar más de un pedido por cliente: " + ex.getMessage());
        }

        sql = "CREATE TABLE IF NOT EXISTS pedidos_item (pedido_id INTEGER NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE, producto_id VARCHAR(255) NOT NULL REFERENCES productos(codigo), cantidad INTEGER NOT NULL, precio_unitario NUMERIC(10,2) NOT NULL, subtotal NUMERIC(10,2) GENERATED ALWAYS AS (cantidad * precio_unitario) STORED, PRIMARY KEY (pedido_id, producto_id))";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("No se pudo crear la tabla de pedidos:"+ex.toString());
        }
    }

    public void AgregarPedido(Pedidos pedido) throws SQLException 
    {
        String obtenerCliente = "SELECT identificacion FROM clientes WHERE identificacion = ? FOR UPDATE";
        String obtenerPedidoPendiente = """
            SELECT id FROM pedidos
            WHERE cliente_id = ? AND estado = ?
            ORDER BY id DESC
            LIMIT 1
            FOR UPDATE
            """;
        String crearPedido = """
            INSERT INTO pedidos (cliente_id, estado)
            VALUES (?, ?)
            RETURNING id
            """;
        String actualizarInventario = """
            UPDATE productos
            SET cantidad = cantidad - ?
            WHERE codigo = ? AND cantidad >= ?
            """;
        String agregarProducto = """
            INSERT INTO pedidos_item (pedido_id, producto_id, cantidad, precio_unitario)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (pedido_id, producto_id)
            DO UPDATE SET cantidad = pedidos_item.cantidad + EXCLUDED.cantidad
            """;

        try (Connection conexion = Conexion.obtenerConexion()) {
            conexion.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conexion.prepareStatement(obtenerCliente)) {
                    ps.setString(1, pedido.getCliente().getIdentificacion());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("El cliente seleccionado ya no existe.");
                        }
                    }
                }

                Integer idPedido = null;
                try (PreparedStatement ps = conexion.prepareStatement(obtenerPedidoPendiente)) {
                    ps.setString(1, pedido.getCliente().getIdentificacion());
                    ps.setString(2, Pedidos.ESTADO_PENDIENTE_PAGO);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            idPedido = rs.getInt("id");
                        }
                    }
                }

                if (idPedido == null) {
                    try (PreparedStatement ps = conexion.prepareStatement(crearPedido)) {
                        ps.setString(1, pedido.getCliente().getIdentificacion());
                        ps.setString(2, Pedidos.ESTADO_PENDIENTE_PAGO);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException("No se pudo crear el pedido.");
                            }
                            idPedido = rs.getInt("id");
                        }
                    }
                }

                pedido.setID(idPedido);
                try (PreparedStatement inventarioPs = conexion.prepareStatement(actualizarInventario);
                     PreparedStatement productoPs = conexion.prepareStatement(agregarProducto)) {
                    for (Producto producto : pedido.getProductos()) {
                        inventarioPs.setInt(1, producto.getCantidad());
                        inventarioPs.setString(2, producto.getCodigo());
                        inventarioPs.setInt(3, producto.getCantidad());
                        if (inventarioPs.executeUpdate() != 1) {
                            throw new SQLException("No hay suficiente inventario para el producto "
                                    + producto.getCodigo() + ".");
                        }

                        productoPs.setInt(1, idPedido);
                        productoPs.setString(2, producto.getCodigo());
                        productoPs.setInt(3, producto.getCantidad());
                        productoPs.setDouble(4, producto.getPrecio());
                        productoPs.executeUpdate();
                    }
                }

                conexion.commit();
            } catch (SQLException ex) {
                conexion.rollback();
                throw ex;
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }

    public int MarcarPedidosPagados(String identificacionCliente) throws SQLException {
        String sql = """
            UPDATE pedidos
            SET estado = ?
            WHERE cliente_id = ? AND estado = ?
              AND EXISTS (
                  SELECT 1 FROM pedidos_item
                  WHERE pedidos_item.pedido_id = pedidos.id
              )
            """;
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, Pedidos.ESTADO_PAGADO);
            ps.setString(2, identificacionCliente);
            ps.setString(3, Pedidos.ESTADO_PENDIENTE_PAGO);
            return ps.executeUpdate();
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
            SELECT p.id, p.estado AS estado_pedido, c.identificacion, c.nombres, c.telefono, c.direccion, i.producto_id, k.descripcion, k.categoria, k.estado, i.precio_unitario AS precio, i.cantidad, i.subtotal, p.fecha_creacion, p.hora_creacion
            FROM pedidos p
            INNER JOIN clientes c ON c.identificacion = p.cliente_id
            INNER JOIN pedidos_item i ON p.id = i.pedido_id
            INNER JOIN productos k ON k.codigo = i.producto_id
            ORDER BY p.id, i.producto_id
            """;
        try (Connection conexion = Conexion.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            Integer id_pedido_anterior = null;
            while (rs.next()) {
                Time hora = rs.getTime("hora_creacion");
                LocalTime horaLocal = hora != null ? hora.toLocalTime() : null;
                int idPedido = rs.getInt("id");
                Producto producto = new Producto(
                    rs.getString("producto_id"),
                    rs.getString("descripcion"),
                    rs.getDouble("precio"),
                    rs.getInt("cantidad"),
                    rs.getString("estado"),
                    rs.getString("categoria"),
                    rs.getDouble("subtotal")
                );
                if (id_pedido_anterior != null && idPedido == id_pedido_anterior)
                {
                    pedidos.get(pedidos.size() - 1).AddProducto(producto);
                    continue;
                }
                id_pedido_anterior = idPedido;
                Cliente cliente = new Cliente(
                    rs.getString("identificacion"),
                    rs.getString("nombres"),
                    rs.getString("telefono"),
                    rs.getString("direccion")
                );
                
                Pedidos pedido = new Pedidos(
                    idPedido,
                    cliente,
                    rs.getDate("fecha_creacion"),
                    horaLocal
                );
                pedido.setEstado(rs.getString("estado_pedido"));
                pedidos.add(pedido);
                pedido.AddProducto(producto);
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
