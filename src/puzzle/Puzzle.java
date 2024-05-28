package puzzle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Puzzle extends JFrame {

    private JPanel panel;
    private BufferedImage source;
    private ArrayList<boton> buttons;
    private ArrayList<Point> solution;
    private Image image;
    private boton lastButton;
    private int width, height;
    private final int DESIRED_WIDTH = 600;
    private BufferedImage resized;
    private long startTime;  // Variable para registrar el tiempo de inicio

    // Constructor de la clase Puzzle
    public Puzzle() throws URISyntaxException {
        initUI();
    }

    // Método para inicializar la interfaz de usuario
    private void initUI() throws URISyntaxException {
        // Inicializar la solución esperada
        solution = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                solution.add(new Point(i, j));
            }
        }

        buttons = new ArrayList<>();
        panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(4, 3, 0, 0));

        try {
            source = loadImage();
            int h = getNewHeight(source.getWidth(), source.getHeight());
            resized = resizeImage(source, DESIRED_WIDTH, h, BufferedImage.TYPE_INT_ARGB);
        } catch (IOException ex) {
            Logger.getLogger(Puzzle.class.getName()).log(Level.SEVERE, null, ex);
        }

        width = resized.getWidth(null);
        height = resized.getHeight(null);

        add(panel, BorderLayout.CENTER);

        // Crear los botones con las imágenes recortadas
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                image = createImage(new FilteredImageSource(resized.getSource(),
                        new CropImageFilter(j * width / 3, i * height / 4,
                                width / 3, height / 4)));
                boton button = new boton(image);
                button.putClientProperty("position", new Point(i, j));

                if (i == 3 && j == 2) {
                    lastButton = new boton();
                    lastButton.setBorderPainted(false);
                    lastButton.setContentAreaFilled(false);
                    lastButton.setLastButton();
                    lastButton.putClientProperty("position", new Point(i, j));
                } else {
                    buttons.add(button);
                }
            }
        }

        // Mezclar los botones y añadir el último botón
        Collections.shuffle(buttons);
        buttons.add(lastButton);

        for (boton btn : buttons) {
            panel.add(btn);
            btn.setBorder(BorderFactory.createLineBorder(Color.gray));
            btn.addActionListener(new ClickAction());
        }

        // Registrar el tiempo de inicio del juego
        startTime = System.currentTimeMillis();

        pack();
        setTitle("Puzzle");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Método para calcular la nueva altura manteniendo la proporción
    private int getNewHeight(int w, int h) {
        double ratio = DESIRED_WIDTH / (double) w;
        return (int) (h * ratio);
    }

    // Método para cargar la imagen
    private BufferedImage loadImage() throws IOException, URISyntaxException {
        BufferedImage bimg = null;
        try {
            bimg = ImageIO.read(new File(getClass().getResource("/images/LavaExpress.jpg").toURI()));
        } catch (IOException e) {
            System.err.println("Error al cargar la imagen: " + e.getMessage());
        }
        return bimg;
    }

    // Método para redimensionar la imagen
    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height, int type) {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    // Clase para manejar las acciones de clic en los botones
    private class ClickAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            checkButton(e);
            checkSolution();
        }

        // Método para verificar y mover el botón clicado si es posible
        private void checkButton(ActionEvent e) {
            int lidx = 0;
            for (boton button : buttons) {
                if (button.isLastButton()) {
                    lidx = buttons.indexOf(button);
                }
            }
            
            //Verificar y realizar el movimiento
            JButton button = (JButton) e.getSource();
            int bidx = buttons.indexOf(button);

            if ((bidx - 1 == lidx && bidx % 3 != 0) || (bidx + 1 == lidx && lidx % 3 != 0)
                    || (bidx - 3 == lidx) || (bidx + 3 == lidx)) {
                Collections.swap(buttons, bidx, lidx);
                updateButtons();
            }
        }

        // Método para actualizar los botones en la interfaz gráfica
        private void updateButtons() {
            panel.removeAll();
            for (JComponent btn : buttons) {
                panel.add(btn);
            }
            panel.validate();
        }

        // Método para verificar si se ha completado el puzzle
        private void checkSolution() {
            ArrayList<Point> current = new ArrayList<>();
            for (JComponent btn : buttons) {
                current.add((Point) btn.getClientProperty("position"));
            }

            if (compareList(solution, current)) {
                long endTime = System.currentTimeMillis();
                long timeTaken = (endTime - startTime) / 1000;  // Tiempo en segundos
                JOptionPane.showMessageDialog(panel, "Finalizado correctamente en " + timeTaken + " segundos",
                        "Felicidades", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // Método para comparar dos listas de puntos
    public static boolean compareList(List<Point> ls1, List<Point> ls2) {
        return ls1.toString().contentEquals(ls2.toString());
    }

    // Método principal para ejecutar el juego
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Puzzle puzzle = null;
            try {
                puzzle = new Puzzle();
            } catch (URISyntaxException ex) {
                Logger.getLogger(Puzzle.class.getName()).log(Level.SEVERE, null, ex);
            }
            puzzle.setVisible(true);
        });
    }
}
