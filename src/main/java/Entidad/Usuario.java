/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidad;
import java.time.LocalDateTime;
/**
 *
 * @author Steven
 */
public class Usuario {
    String username;
    String password;
    String rol;
    LocalDateTime fecha_creacion;
    
    
    public Usuario(String user, String rol, LocalDateTime fecha_creacion) {
        this.username = user;
        this.rol = rol;
        this.fecha_creacion = fecha_creacion;
    }

    public Usuario(String user, String password, String rol) {
        this.username = user;
        this.password = password;
        this.rol = rol;
    }

    public String getRol() {
        return rol;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public LocalDateTime getfecha_creacion()
    {
        return fecha_creacion;
    }
    
    public String getPassword() {
        return password;
    }
}
