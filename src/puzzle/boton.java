package puzzle;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

class boton extends JButton {

    private boolean isLastButton;

    // Constructor por defecto
    public boton() {
        super();
        initUI();
    }

    // Constructor con imagen
    public boton(Image image) {
        super(new ImageIcon(image));
        initUI();
    }

    // Método para inicializar la interfaz del botón
    private void initUI() {
        isLastButton = false;
        BorderFactory.createLineBorder(Color.gray);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.yellow));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createLineBorder(Color.gray));
            }
        });
    }

    // Método para establecer el botón como el último botón
    public void setLastButton() {
        isLastButton = true;
    }

    // Método para verificar si el botón es el último botón
    public boolean isLastButton() {
        return isLastButton;
    }
}
