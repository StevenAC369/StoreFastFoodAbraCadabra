/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Entidad.Producto;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import java.util.*;

/**
 *
 * @author Steven
 */
public class ProductosDAO {
    
    public ProductosDAO()
    {
        String sql = "CREATE TABLE IF NOT EXISTS productos(codigo VARCHAR(15) PRIMARY KEY, descripcion VARCHAR(255), precio NUMERIC(10,2), cantidad INTEGER, estado VARCHAR(10), categoria VARCHAR(15))";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (ps.executeUpdate() > 0) {
                    System.out.println("Se ha creado la tabla de productos");
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString()+": No se pudo crear la tabla de productos:");
            
        }
    }
    
    public void AgregarProducto(Producto producto)
    {
        String sql = "INSERT INTO productos (codigo, descripcion, precio, cantidad, estado, categoria) VALUES (?, ?, ?,?, ?, ?)";
        try (Connection conexion = Conexion.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getDescripcion());
            ps.setDouble(3, producto.getPrecio());
            ps.setInt(4, producto.getCantidad());
            ps.setString(5, producto.getEstado());
            ps.setString(6, producto.getCategoria());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Se ha guardado correctamente", "Database", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, "Error al guardar: "+ex.toString(), "Database", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public List<Producto> ListarProductos()
    {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos";
        try (Connection conexion = Conexion.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs= ps.executeQuery())
            {
                while(rs.next())
                {
                    Producto producto = new Producto(
                            rs.getString("codigo"),
                            rs.getString("descripcion"),
                            rs.getDouble("precio"),
                            rs.getInt("cantidad"),
                            rs.getString("estado"),
                            rs.getString("categoria")
                    );
                    productos.add(producto);
                }
            return productos;
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, "Error al Listar: "+ex.getMessage(), "Database", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    public void EliminarProducto(Producto producto)
    {
        String sql = "DELETE FROM productos WHERE codigo=?";
        try (Connection conexion = Conexion.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, producto.getCodigo());
            int update = ps.executeUpdate();
            if (update >0)
            {
                JOptionPane.showMessageDialog(null, "Se ha eliminado correctamente", "Database", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, "Error al eliminar", "Database", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void ActualizarProducto(String codigo_antiguo, Producto producto_nuevo)
    {
        String sql = "UPDATE productos SET codigo = ?, descripcion = ?, precio = ?, "
                + "cantidad = ?, estado = ?, categoria = ? WHERE codigo = ?";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, producto_nuevo.getCodigo());
            ps.setString(2, producto_nuevo.getDescripcion());
            ps.setDouble(3, producto_nuevo.getPrecio());
            ps.setInt(4, producto_nuevo.getCantidad());
            ps.setString(5, producto_nuevo.getEstado());
            ps.setString(6, producto_nuevo.getCategoria());
            ps.setString(7, codigo_antiguo);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Se ha actualizado correctamente",
                        "Database", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el producto: " + ex.getMessage(),
                    "Database", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public List<Producto> BuscarProducto(String codigo, String parametro1, String parametro2, Boolean SimilarTO)
    {
        List<Producto> productos = new ArrayList<>();
        List<String> WhiteList = new ArrayList<>();
        WhiteList.add("categoria");
        WhiteList.add("codigo");
        WhiteList.add("descripcion");
        WhiteList.add("cantidad");
        WhiteList.add("estado");
        WhiteList.add("categoria");
        String search="SIMILAR TO";
        
        if (!WhiteList.contains(parametro1)) {
            parametro1 = "codigo"; 
        }
        
        if (!SimilarTO)
        {
            search="=";
        }
        
        if (parametro2 != null && !WhiteList.contains(parametro2)) {
            parametro2 = "descripcion"; 
        }
        String sql = "SELECT * FROM productos WHERE " + parametro1 + " " + search
                + " ? OR " + parametro2 + " " + search + " ?";
        
        if(parametro2==null)
        {
            sql = "SELECT * FROM productos WHERE " + parametro1 + " " + search + " ?";
        }
        try (Connection conexion = Conexion.obtenerConexion();
            PreparedStatement ps = conexion.prepareStatement(sql))
            {
                { 
                    ps.setString(1, codigo);
                    if(parametro2!=null)
                    {
                        ps.setString(2, codigo);   
                    }   
                }
            
            ResultSet rs= ps.executeQuery();
            while(rs.next())
                {
                Producto producto = new Producto(
                        rs.getString("codigo"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getInt("cantidad"),
                        rs.getString("estado"),
                        rs.getString("categoria")
                    );
                    productos.add(producto);
                }
            return productos;
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, "Error al Buscar: "+ex.toString(), "Database", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}
