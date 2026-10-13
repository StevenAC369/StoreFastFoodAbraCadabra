/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidad;

/**
 *
 * @author Steven
 */
public class Cliente {
    String identificacion;
    String nombres;
    String telefono;
    String direccion;
    

    public Cliente(String identificacion, String nombres, String telefono, String direccion) {
        this.identificacion = identificacion;
        this.nombres = nombres;
        this.telefono = telefono;
        this.direccion = direccion;
    }
    
    public Cliente()
    {
        
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public String getNombres() {
        return nombres;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDireccion() {
        return direccion;
    }
}
