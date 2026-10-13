package projects.abracadabra.mavenproject2.Vista.CRUD_Interface;


import Entidad.Pedidos;

import javax.swing.JOptionPane;

import Entidad.Producto;
import javax.swing.JTable;
import Servicios.*;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JSpinner;
import javax.swing.table.DefaultTableModel;



public class PedidosViewManager {
        
        private static Producto producto_a_agregar;

        public static void AgregarPedido(JTable tabla_producto, JSpinner contador) {
            if(ServiciosCliente.cliente_a_agregar == null) {
                JOptionPane.showMessageDialog(null, "Cliente no seleccionado o incompleto, por favor seleccione un cliente para realizar el pedido",
                        "Servicios", JOptionPane.WARNING_MESSAGE);
                return;
            }
        try
        {
                     
            Integer cantidad_a_agregar = Integer.parseInt(contador.getValue().toString());
            
        
            int filaSeleccionada = tabla_producto.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(null, "Seleccione un producto para agregar",
                        "Servicios", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Object codigo_producto = tabla_producto.getValueAt(filaSeleccionada, 0);
        
            if (codigo_producto == null) {
                JOptionPane.showMessageDialog(null, "La fila seleccionada no tiene identificación",
                        "Servicios", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (ServiciosCliente.cliente_a_agregar.getIdentificacion() == null)
            {
                JOptionPane.showMessageDialog(null, "Seleccione un cliente para realizar el pedido",
                        "Servicios", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            
            Object descripcion_producto = tabla_producto.getValueAt(filaSeleccionada, 1);
            Object precio_producto = tabla_producto.getValueAt(filaSeleccionada, 2);
            Object cantidad_producto = tabla_producto.getValueAt(filaSeleccionada, 3);
            
            
            if (Integer.parseInt(cantidad_producto.toString())<cantidad_a_agregar)
            {
                JOptionPane.showMessageDialog(null, "La cantidad no puede ser mayor a la de productos disponibles",
                        "Servicios", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            
            Object estado_producto = tabla_producto.getValueAt(filaSeleccionada, 4);
            Object categoria_producto = tabla_producto.getValueAt(filaSeleccionada, 5);
            
            producto_a_agregar = new Producto(codigo_producto.toString(), descripcion_producto.toString(),Double.parseDouble(precio_producto.toString()),Integer.parseInt(cantidad_producto.toString()),estado_producto.toString(),categoria_producto.toString());
            producto_a_agregar.setCantidad(cantidad_a_agregar);
            List<Producto> productos_a_agregar = new ArrayList<>();
            productos_a_agregar.add(producto_a_agregar);
            
            ServiciosPedidos.AgregarPedido(productos_a_agregar, ServiciosCliente.cliente_a_agregar, Integer.parseInt(cantidad_producto.toString()));

            ProductosViewManager.CargarTabla(tabla_producto);
            
        
        }
        catch (NumberFormatException ex)
        {
            JOptionPane.showMessageDialog(null, "La cantidad debe ser un número entero",
                    "Servicios", JOptionPane.ERROR_MESSAGE);
        }
        
        }

        
        static public void CargarTablaPedidos(JTable tabla)
        {
            try
            {
                List<Pedidos> pedidos = ServiciosPedidos.MostrarPedidos();
                DefaultTableModel modelo = new DefaultTableModel();
                modelo.addColumn("Id de Pedido");
                modelo.addColumn("Id de Cliente");
                modelo.addColumn("Nombre de Cliente");
                modelo.addColumn("Codigo de Producto");
                modelo.addColumn("Descripcion");
                modelo.addColumn("Precio Unitario");
                modelo.addColumn("Cantidad");
                modelo.addColumn("Subtotal");
                for (Pedidos pedido : pedidos) {
                    
                    for (Producto producto : pedido.getProductos()) {

                        modelo.addRow(new Object[]{
                            pedido.getID(),
                            pedido.getCliente().getIdentificacion(),
                            pedido.getCliente().getNombres(),
                            producto.getCodigo(),
                            producto.getDescripcion(),
                            producto.getPrecio(),
                            producto.getCantidad(),
                            producto.getSub()
                        });
                        
                    }
                }
                tabla.setModel(modelo);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "No se pudo cargar la tabla Clientes: "
                    + ex.getMessage(), "Servicios", JOptionPane.ERROR_MESSAGE);
            }
        
        }
        
        
        
}
