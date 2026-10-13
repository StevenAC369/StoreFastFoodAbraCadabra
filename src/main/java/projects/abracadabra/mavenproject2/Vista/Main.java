/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projects.abracadabra.mavenproject2.Vista;

/**
 *
 * @author Steven
 */
public class Main {
   

     public static void main(String[] args) {
        try {
           javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
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

