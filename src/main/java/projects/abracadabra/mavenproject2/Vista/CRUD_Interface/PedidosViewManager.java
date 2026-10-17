package projects.abracadabra.mavenproject2.Vista.CRUD_Interface;

import Entidad.Pedidos;
import Entidad.Producto;
import Servicios.ServiciosCliente;
import Servicios.ServiciosPedidos;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class PedidosViewManager {

    private static Producto producto_a_agregar;

    public static boolean AgregarPedido(JTable tabla_producto, JSpinner contador) {
        if (ServiciosCliente.cliente_a_agregar == null) {
            JOptionPane.showMessageDialog(null,
                    "Cliente no seleccionado o incompleto, por favor seleccione un cliente para realizar el pedido",
                    "Servicios", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            int cantidadAAgregar = Integer.parseInt(contador.getValue().toString());
            if (cantidadAAgregar < 1) {
                JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor que cero",
                        "Servicios", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            int filaSeleccionada = tabla_producto.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(null, "Seleccione un producto para agregar",
                        "Servicios", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            Object codigoProducto = tabla_producto.getValueAt(filaSeleccionada, 0);
            if (codigoProducto == null) {
                JOptionPane.showMessageDialog(null, "La fila seleccionada no tiene identificación",
                        "Servicios", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (ServiciosCliente.cliente_a_agregar.getIdentificacion() == null) {
                JOptionPane.showMessageDialog(null, "Seleccione un cliente para realizar el pedido",
                        "Servicios", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            String descripcion = tabla_producto.getValueAt(filaSeleccionada, 1).toString();
            double precio = Double.parseDouble(tabla_producto.getValueAt(filaSeleccionada, 2).toString());
            int cantidadDisponible = Integer.parseInt(tabla_producto.getValueAt(filaSeleccionada, 3).toString());
            if (cantidadDisponible < cantidadAAgregar) {
                JOptionPane.showMessageDialog(null, "La cantidad no puede ser mayor a la de productos disponibles",
                        "Servicios", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            String estado = tabla_producto.getValueAt(filaSeleccionada, 4).toString();
            String categoria = tabla_producto.getValueAt(filaSeleccionada, 5).toString();
            producto_a_agregar = new Producto(codigoProducto.toString(), descripcion, precio,
                    cantidadAAgregar, estado, categoria);
            List<Producto> productosAAgregar = new ArrayList<>();
            productosAAgregar.add(producto_a_agregar);

            ServiciosPedidos.AgregarPedido(productosAAgregar, ServiciosCliente.cliente_a_agregar);
            return true;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "La cantidad y el precio deben tener un formato válido",
                    "Servicios", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No se pudo guardar el pedido: " + ex.getMessage(),
                    "Servicios", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public static void CargarTablaPedidos(JTable tabla) {
        try {
            List<Pedidos> pedidos = ServiciosPedidos.MostrarPedidos();
            DefaultTableModel modelo = CrearModeloPedidos();
            for (Pedidos pedido : pedidos) {
                if (!Pedidos.ESTADO_PENDIENTE_PAGO.equals(pedido.getEstado())) {
                    continue;
                }
                for (Producto producto : pedido.getProductos()) {
                    AgregarFilaPedido(modelo, pedido, producto);
                }
            }
            tabla.setModel(modelo);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "No se pudo cargar la tabla de pedidos: "
                    + ex.getMessage(), "Servicios", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void FiltrarPedidoConClienteActual(JLabel subtotal, JLabel iva, JLabel total,
            JTable tabla, JTextField identificacion) {
        BigDecimal subtotalPendiente = BigDecimal.ZERO;
        String identificacionSeleccionada = identificacion.getText().trim();
        DefaultTableModel modelo = CrearModeloPedidos();

        for (Pedidos pedido : ServiciosPedidos.MostrarPedidos()) {
            if (Pedidos.ESTADO_PENDIENTE_PAGO.equals(pedido.getEstado())
                    && identificacionSeleccionada.equals(pedido.getCliente().getIdentificacion())) {
                for (Producto producto : pedido.getProductos()) {
                    AgregarFilaPedido(modelo, pedido, producto);
                    subtotalPendiente = subtotalPendiente.add(ObtenerSubtotal(producto));
                }
            }
        }

        tabla.setModel(modelo);
        BigDecimal ivaPendiente = subtotalPendiente
                .multiply(new BigDecimal("0.15"))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPendiente = subtotalPendiente.add(ivaPendiente);
        subtotal.setText(FormatearImporte(subtotalPendiente));
        iva.setText(FormatearImporte(ivaPendiente));
        total.setText(FormatearImporte(totalPendiente));
    }

    public static void CobrarPedido(JLabel subtotal, JLabel iva, JLabel total,
            JTable tabla, JTextField identificacion) {
        String identificacionCliente = identificacion.getText().trim();
        if (identificacionCliente.isEmpty() || ServiciosCliente.cliente_a_agregar == null) {
            JOptionPane.showMessageDialog(null, "Seleccione un cliente antes de cobrar.",
                    "Servicios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                null,
                "¿Confirmar el cobro de " + total.getText() + "?",
                "Confirmar cobro",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int pedidosPagados = ServiciosPedidos.MarcarPedidosPagados(identificacionCliente);
            if (pedidosPagados == 0) {
                JOptionPane.showMessageDialog(null, "El cliente no tiene pedidos pendientes de pago.",
                        "Servicios", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            FiltrarPedidoConClienteActual(subtotal, iva, total, tabla, identificacion);
            JOptionPane.showMessageDialog(null, "El pedido se marcó como pagado.",
                    "Servicios", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No se pudo registrar el cobro: " + ex.getMessage(),
                    "Servicios", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static DefaultTableModel CrearModeloPedidos() {
        return new DefaultTableModel(new String[]{
            "ID de pedido",
            "ID de cliente",
            "Nombre del cliente",
            "Código del producto",
            "Descripción",
            "Precio unitario",
            "Cantidad",
            "Subtotal",
            "Estado del pedido"
        }, 0);
    }

    private static void AgregarFilaPedido(DefaultTableModel modelo, Pedidos pedido, Producto producto) {
        modelo.addRow(new Object[]{
            pedido.getID(),
            pedido.getCliente().getIdentificacion(),
            pedido.getCliente().getNombres(),
            producto.getCodigo(),
            producto.getDescripcion(),
            producto.getPrecio(),
            producto.getCantidad(),
            ObtenerSubtotal(producto).setScale(2, RoundingMode.HALF_UP),
            pedido.getEstado()
        });
    }

    private static BigDecimal ObtenerSubtotal(Producto producto) {
        if (producto.getSub() != null) {
            return BigDecimal.valueOf(producto.getSub());
        }
        return BigDecimal.valueOf(producto.getPrecio())
                .multiply(BigDecimal.valueOf(producto.getCantidad()));
    }

    private static String FormatearImporte(BigDecimal importe) {
        DecimalFormat formato = new DecimalFormat(
                "$ #,##0.00",
                DecimalFormatSymbols.getInstance(Locale.US)
        );
        return formato.format(importe.setScale(2, RoundingMode.HALF_UP));
    }
}
