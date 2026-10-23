/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servicios;

import Data.BodegaDAO;
import Entidad.Bodega;
import java.util.List;

/**
 *
 * @author Piero
 */
public class ServicioBodega {
     static BodegaDAO database_bodega = new BodegaDAO();
    
    public static List<Bodega> CargarTabla()
    {
        return database_bodega.ListarBodega();
        
    }

    public static Iterable<Bodega> ListarBodega() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
