/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidad;
import java.time.LocalTime;
import java.util.*;
/**
 *
 * @author Steven
 */
public class Pedidos {
    public static final String ESTADO_PENDIENTE_PAGO = "Pendiente de pago";
    public static final String ESTADO_PAGADO = "Pagado";

    Integer ID;
    Cliente cliente;
    List<Producto> productos;
    Double SubTotal=0.0;
    String estado;
    Date fecha_creacion;
    LocalTime hora_creacion;
    public Pedidos() {
        this.productos = new ArrayList<>();
        this.estado = ESTADO_PENDIENTE_PAGO;
    }


    public Pedidos(Integer ID, Cliente cliente, List<Producto> productos, Double SubTotal) {
        this.ID = ID;
        this.cliente = cliente;
        this.productos = new ArrayList<>();
        this.SubTotal = SubTotal;
        this.estado = ESTADO_PENDIENTE_PAGO;
    }

    public Pedidos(Integer ID, Cliente cliente, Date fecha_creacion, LocalTime hora_creacion) {
        this.ID = ID;
        this.cliente = cliente;
        this.productos = new ArrayList<>();
        this.fecha_creacion = fecha_creacion;
        this.hora_creacion = hora_creacion;
        this.estado = ESTADO_PENDIENTE_PAGO;
    }
    
    public void AddProducto(Producto... producto)
    {
        for(Producto producto1: producto)
        {
             productos.add(producto1);
        }
       
    }

    

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    
    
    public Cliente getCliente() {
        return cliente;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public Double getSubTotal() {
        return SubTotal;
    }

    public String getEstado() {
        return estado;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
}
