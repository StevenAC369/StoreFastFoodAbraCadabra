/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Data;

import Entidad.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Steven
 */
public class ClienteDAO {

    private static final List<String> COLUMNAS_BUSQUEDA = Arrays.asList(
            "identificacion", "nombres", "telefono", "direccion");

    public ClienteDAO() 
    {
        String sql = "CREATE TABLE IF NOT EXISTS clientes (identificacion VARCHAR(20) PRIMARY KEY, nombres VARCHAR(255), telefono VARCHAR(30), direccion VARCHAR(255))";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (ps.executeUpdate() >1)
            {
                sql = "INSERT INTO clientes (identificacion, nombres, telefono, direccion) "
                + "VALUES (?, ?, ?, ?)";
                try (Connection conexion1 = Conexion.obtenerConexion();
                    PreparedStatement ps1 = conexion1.prepareStatement(sql)) {
                    ps1.setString(1, "9999999999");
                    ps1.setString(2, "Consumidor Final");
                    ps1.setString(3, "9999999999");
                    ps1.setString(4, "9999999999");
                    ps1.executeUpdate();
                    System.out.println("Se ha crea cliente final");
                    
                } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error al guardar el cliente final: " + ex.getMessage(),
                    "Database", JOptionPane.ERROR_MESSAGE);
                }
            };
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo crear la tabla de clientes: " + ex.getMessage(),
                    "Database", JOptionPane.ERROR_MESSAGE);
        }
    }
   

    public void AgregarCliente(Cliente cliente) {
        String sql = "INSERT INTO clientes (identificacion, nombres, telefono, direccion) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cliente.getIdentificacion());
            ps.setString(2, cliente.getNombres());
            ps.setString(3, cliente.getTelefono());
            ps.setString(4, cliente.getDireccion());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Se ha guardado correctamente",
                    "Database", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al guardar el cliente: " + ex.getMessage(),
                    "Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Cliente> ListarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT identificacion, nombres, telefono, direccion FROM clientes";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clientes.add(new Cliente(
                        rs.getString("identificacion"),
                        rs.getString("nombres"),
                        rs.getString("telefono"),
                        rs.getString("direccion")));
            }
            Cliente cliente_final = new Cliente("9999999999", "Consumidor Final", "9999999999", "9999999999");
            clientes.remove(cliente_final);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al listar los clientes: " + ex.getMessage(),
                    "Database", JOptionPane.ERROR_MESSAGE);
        }
        return clientes;
    }

    public void EliminarCliente(Cliente cliente) {
        String sql = "DELETE FROM clientes WHERE identificacion = ?";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, cliente.getIdentificacion());
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Se ha eliminado correctamente",
                        "Database", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al eliminar el cliente: " + ex.getMessage(),
                    "Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void ActualizarCliente(String identificacionAntigua, Cliente clienteNuevo) {
        String sql = "UPDATE clientes SET identificacion = ?, nombres = ?, telefono = ?, "
                + "direccion = ? WHERE identificacion = ?";
        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, clienteNuevo.getIdentificacion());
            ps.setString(2, clienteNuevo.getNombres());
            ps.setString(3, clienteNuevo.getTelefono());
            ps.setString(4, clienteNuevo.getDireccion());
            ps.setString(5, identificacionAntigua);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Se ha actualizado correctamente",
                        "Database", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el cliente: " + ex.getMessage(),
                    "Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Cliente> BuscarCliente(String busqueda, String parametro1, String parametro2, Boolean SIMILAR_TO) {
        List<Cliente> clientes = new ArrayList<>();
        String search = "=";
        if (SIMILAR_TO) {
            search = "SIMILAR TO";
        }

        if (!COLUMNAS_BUSQUEDA.contains(parametro1)) {
            parametro1 = "identificacion";
        }
        if (parametro2 != null && !COLUMNAS_BUSQUEDA.contains(parametro2)) {
            parametro2 = "nombres";
        }

        String sql = "SELECT identificacion, nombres, telefono, direccion FROM clientes WHERE "
                + parametro1 + " " + search + " ?";
        if (parametro2 != null) {
            sql += " OR " + parametro2 + " " + search + " ?";
        }

        try (Connection conexion = Conexion.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, busqueda);
            if (parametro2 != null) {
                ps.setString(2, busqueda);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clientes.add(new Cliente(
                            rs.getString("identificacion"),
                            rs.getString("nombres"),
                            rs.getString("telefono"),
                            rs.getString("direccion")));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al buscar clientes: " + ex.getMessage(),
                    "Database", JOptionPane.ERROR_MESSAGE);
        }
        return clientes;
    }

}
