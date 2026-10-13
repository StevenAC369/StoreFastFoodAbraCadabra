/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidad;

/**
 *
 * @author Steven
 */
public class Producto {
    String codigo;
    String descripcion;
    Double precio;
    Integer cantidad;
    String estado;
    String categoria;
    Double sub;

    public Producto(String codigo, String descripcion, Double precio, Integer cantidad, String estado, String categoria) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.cantidad = cantidad;
        this.estado = estado;
        this.categoria = categoria;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public String getEstado() {
        return estado;
    }
    
    public String getCategoria()
    {
        return categoria;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    public Double getSub() {
        return sub;
    }
    
    public void setSub(Double sub) {
        this.sub = sub;
    }
}
