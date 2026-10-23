/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projects.abracadabra.mavenproject2.Vista;

import java.io.IOException;


/**
 *
 * @author Steven
 */
public class Main {
   

     public static void main(String[] args) {
        // Ejecuta las ventanas aadaptandose a la interfaz del sistema
        try {
           javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
           // Levanta el docker compose
           ProcessBuilder builder = new ProcessBuilder();
           builder.command("cmd.exe", "/c", "docker-compose -f docker-compose.yml up -d");
           
           builder.start();
          

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException | IOException  ex) {
            java.util.logging.Logger.getLogger(Master.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } 
        try
        {
             
            javax.swing.SwingUtilities.invokeLater(() -> {
             Master login = new Master();
             login.setLocationRelativeTo(null);
             login.setVisible(true);
         }); 
         }
         catch (Exception ex)
         {
            System.out.println("Error:"+ex.toString());
         }
         
         
    }
}
