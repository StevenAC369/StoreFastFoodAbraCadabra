package projects.abracadabra.mavenproject2.Vista.SubPantallasDeAjustes;

import javax.swing.JTextField;

public class ViewAgregarMiembro {
    public static void AgregarUsuuario(JTextField jTextFieldUsuario, JTextField jTextFieldPassword, JTextField jTextFieldPassword2, String rol) {
        String usuario = jTextFieldUsuario.getText();
        String password = jTextFieldPassword.getText();
        String password2 = jTextFieldPassword2.getText();

        if (usuario.isEmpty() || password.isEmpty() || password2.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(password2)) {
            javax.swing.JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        Entidad.Usuario nuevoUsuario = new Entidad.Usuario(usuario, password, rol);

        try {
            Servicios.ServiciosUsuario.AgregarUsuario(nuevoUsuario);
            javax.swing.JOptionPane.showMessageDialog(null, "Miembro agregado exitosamente.", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error al agregar el miembro: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        
    }
}
