/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Data;

import Entidad.Bodega;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Piero
 */
public class BodegaDAO {
    public BodegaDAO()
    {
    String sql = "CREATE TABLE IF NOT EXISTS solicitud_traslado ("
                + "id_solicitud INT AUTO_INCREMENT PRIMARY KEY, "
                + "codigo_producto VARCHAR(50) NOT NULL, "
                + "cantidad INT NOT NULL, "
                + "FOREIGN KEY (codigo_producto) REFERENCES producto(codigo) ON DELETE CASCADE"
                + ");";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (ps.executeUpdate() > 0) {
                    System.out.println("Se ha creado la tabla de bodega");
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString()+": No se pudo crear la tabla de bodega:");
            
        }


        sql="CREATE TABLE IF NOT EXISTS bodega (" + 
                        "id SERIAL PRIMARY KEY, " + 
                        "codigo VARCHAR(50) NOT NULL, "+
                        "descripcion TEXT, " + 
                        "cantidad INT, " + 
                        "sucursal VARCHAR(50)" + 
                        ");";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (ps.executeUpdate() > 0) {
                    System.out.println("Se ha creado la tabla de bodega");
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString()+": No se pudo crear la tabla de bodega:");
            
        }
    }
    public List<Bodega> ListarBodega() {
    List<Bodega> lista = new ArrayList<>();
    
    String sql = "SELECT b.id, b.codigo, p.descripcion, b.cantidad, b.sucursal " +
                 "FROM bodega b " +
                 "INNER JOIN productos p ON b.codigo = p.codigo " +
                 "ORDER BY b.id DESC";

    try (Connection con = Conexion.obtenerConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Bodega b = new Bodega();
            b.setId(rs.getInt("id"));
            b.setCodigo(rs.getString("codigo"));
            b.setDescripcion(rs.getString("descripcion"));
            b.setCantidad(rs.getInt("cantidad"));
            b.setSucursal(rs.getString("sucursal"));
            
            lista.add(b);
        }

    } catch (SQLException ex) {
        System.err.println("Error al listar solicitudes en bodega: " + ex.getMessage());
    }
    
    return lista;
}

public boolean agregarSolicitudProducto(String codigoProducto, int cantidad, String sucursal) {
    String sqlVerificarStock = "SELECT cantidad FROM productos WHERE codigo = ?";
    String sqlRestarStock = "UPDATE productos SET cantidad = cantidad - ? WHERE codigo = ?";
    String sqlBuscarBodega = "SELECT id FROM bodega WHERE codigo = ? AND sucursal = ?";
    String sqlActualizarBodega = "UPDATE bodega SET cantidad = cantidad + ? WHERE id = ?";
    String sqlInsertarBodega = "INSERT INTO bodega (codigo, cantidad, sucursal) VALUES (?, ?, ?)";

    boolean exito = false;
    Connection con = null;

    try {
        con = Conexion.obtenerConexion();
        if (con != null) {
            con.setAutoCommit(false); // Transacción atómica

            // 1. Verificar stock disponible en inventario
            try (PreparedStatement psCheckStock = con.prepareStatement(sqlVerificarStock)) {
                psCheckStock.setString(1, codigoProducto);
                ResultSet rsStock = psCheckStock.executeQuery();

                if (rsStock.next()) {
                    int stockDisponible = rsStock.getInt("cantidad");
                    if (stockDisponible < cantidad) {
                        System.err.println("Stock insuficiente.");
                        con.rollback();
                        return false;
                    }
                } else {
                    con.rollback();
                    return false;
                }
            }

            // 2. Restar la cantidad transferida del inventario general
            try (PreparedStatement psRestar = con.prepareStatement(sqlRestarStock)) {
                psRestar.setInt(1, cantidad);
                psRestar.setString(2, codigoProducto);
                psRestar.executeUpdate();
            }

            // 3. Registrar o acumular la solicitud hacia la sucursal seleccionada
            try (PreparedStatement psCheckBodega = con.prepareStatement(sqlBuscarBodega)) {
                psCheckBodega.setString(1, codigoProducto);
                psCheckBodega.setString(2, sucursal);
                ResultSet rsBodega = psCheckBodega.executeQuery();

                if (rsBodega.next()) {
                    int idBodega = rsBodega.getInt("id");
                    try (PreparedStatement psUpdate = con.prepareStatement(sqlActualizarBodega)) {
                        psUpdate.setInt(1, cantidad);
                        psUpdate.setInt(2, idBodega);
                        psUpdate.executeUpdate();
                    }
                } else {
                    try (PreparedStatement psInsert = con.prepareStatement(sqlInsertarBodega)) {
                        psInsert.setString(1, codigoProducto);
                        psInsert.setInt(2, cantidad);
                        psInsert.setString(3, sucursal);
                        psInsert.executeUpdate();
                    }
                }
            }

            con.commit();
            exito = true;
        }
    } catch (SQLException e) {
        System.err.println("Error al procesar solicitud: " + e.getMessage());
        if (con != null) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    } finally {
        if (con != null) {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    return exito;
}
    public boolean eliminarSolicitudProducto(int idSolicitud, String codigoProducto, int cantidadADevolver) 
    {
    String sqlEliminarBodega = "DELETE FROM bodega WHERE id = ?";
    String sqlDevolverStock = "UPDATE productos SET cantidad = cantidad + ? WHERE codigo = ?";

    boolean exito = false;
    Connection con = null;

    try {
        con = Conexion.obtenerConexion();
        if (con != null) {
            con.setAutoCommit(false); // Iniciar transacción

            // 1. Eliminar la solicitud de la tabla bodega
            try (PreparedStatement psDelete = con.prepareStatement(sqlEliminarBodega)) {
                psDelete.setInt(1, idSolicitud);
                psDelete.executeUpdate();
            }

            // 2. Devolver la cantidad al stock de la tabla productos
            try (PreparedStatement psSumar = con.prepareStatement(sqlDevolverStock)) {
                psSumar.setInt(1, cantidadADevolver);
                psSumar.setString(2, codigoProducto);
                psSumar.executeUpdate();
            }

            con.commit(); // Confirmar ambos cambios
            exito = true;
        }
    } catch (SQLException e) {
        System.err.println("Error al eliminar la solicitud: " + e.getMessage());
        if (con != null) {
            try {
                con.rollback(); // Revertir en caso de error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    } finally {
        if (con != null) {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    return exito;
}
    public boolean registrarSolicitud(String idSolicitud, String codigo, String descripcion, int cantidad, String sucursal) throws SQLException {
    String sql = "INSERT INTO solicitudes (id_solicitud, codigo_producto, descripcion, cantidad, sucursal) VALUES (?, ?, ?, ?, ?)";
     try (Connection conexion = Conexion.obtenerConexion();
        PreparedStatement ps = conexion.prepareStatement(sql)) {
        ps.setString(1, idSolicitud);
        ps.setString(2, codigo);
        ps.setString(3, descripcion);
        ps.setInt(4, cantidad);
        ps.setString(5, sucursal);
        return ps.executeUpdate() > 0;
    }
}
}