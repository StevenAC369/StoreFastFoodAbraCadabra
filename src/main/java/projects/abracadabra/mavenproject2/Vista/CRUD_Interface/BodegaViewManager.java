/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projects.abracadabra.mavenproject2.Vista.CRUD_Interface;

import Data.ProductosDAO;
import Data.BodegaDAO;
import Entidad.Bodega;
import java.util.List;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Steven
 */
public class BodegaViewManager {
    
    public static void CargarTablaPlanificacion(JTable tabla) {
        try {
            // Columnas sin Sucursal para la tabla intermedia
            String[] columnas = {"ID Solicitud", "Código", "Descripción", "Cantidad"};
            
            DefaultTableModel modelo_tabla = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Evita edición directa en celdas
                }
            };

            tabla.setModel(modelo_tabla);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al inicializar la tabla de planificación: " + ex.getMessage(), "Vista", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void CargarTablaSolicitudesRecientes(JTable tabla) {
        try {
            // Columnas completas con Sucursal para el historial
            String[] columnas = {"ID Solicitud", "Código", "Descripción", "Cantidad", "Sucursal"};
            
            DefaultTableModel modelo_tabla = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            BodegaDAO dao = new BodegaDAO();
            List<Bodega> lista = dao.ListarBodega();

            for (Bodega b : lista) {
                modelo_tabla.addRow(new Object[]{
                    "SOL-" + String.format("%03d", b.getId()),
                    b.getCodigo(),
                    b.getDescripcion(),
                    b.getCantidad(),
                    b.getSucursal() // Se muestra únicamente aquí
                });
            }

            tabla.setModel(modelo_tabla);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "No se pudo cargar la tabla de Solicitudes Recientes: " + ex.getMessage(), "Vista", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}

