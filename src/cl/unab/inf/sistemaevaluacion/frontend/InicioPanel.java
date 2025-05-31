package cl.unab.inf.sistemaevaluacion.frontend;

import cl.unab.inf.sistemaevaluacion.backend.Controlador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class InicioPanel extends JPanel {
    private final Controlador controlador;
    private final JLabel tituloLabel;
    private final JLabel estadoCargaLabel;
    private final JLabel cantidadItemsLabel;
    private final JLabel tiempoTotalLabel;
    private final JButton cargarArchivoButton;
    private final JButton iniciarPruebaButton;

    public InicioPanel(Controlador controlador) {
        this.controlador = controlador;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        tituloLabel = new JLabel("Sistema de Evaluación");
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 36));
        tituloLabel.setHorizontalAlignment(SwingConstants.CENTER);

        estadoCargaLabel = new JLabel("Seleccione un archivo para comenzar.");
        estadoCargaLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        estadoCargaLabel.setHorizontalAlignment(SwingConstants.CENTER);

        cantidadItemsLabel = new JLabel("Cantidad de ítems: -");
        cantidadItemsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        cantidadItemsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        tiempoTotalLabel = new JLabel("Tiempo estimado total: -");
        tiempoTotalLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        tiempoTotalLabel.setHorizontalAlignment(SwingConstants.CENTER);

        cargarArchivoButton = new JButton("Cargar archivo.");
        iniciarPruebaButton = new JButton("Iniciar prueba");
        iniciarPruebaButton.setEnabled(false);

        cargarArchivoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seleccionarYEnviarArchivo();
            }
        });

        iniciarPruebaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controlador.iniciarPrueba();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);

        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(tituloLabel, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridy++;
        add(estadoCargaLabel, gbc);

        gbc.gridy++;
        add(cantidadItemsLabel, gbc);

        gbc.gridy++;
        add(tiempoTotalLabel, gbc);

        gbc.insets = new Insets(30, 0, 10, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.gridy++;
        add(cargarArchivoButton, gbc);

        gbc.insets = new Insets(10, 0, 0, 0);
        gbc.gridy++;
        add(iniciarPruebaButton, gbc);
    }

    private void seleccionarYEnviarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        // Opcional: Establecer un filtro para archivos .txt
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos de Texto (*.txt)", "txt"));

        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            // Ya no necesitas la variable 'cargado' aquí, la lógica de éxito/error se maneja con try-catch
            try {
                controlador.cargarItemsDesdeArchivo(archivo);
                // Si la carga fue exitosa, el controlador notificará a sus observadores
                // (incluyendo VentanaPrincipal), y VentanaPrincipal.datosCargados()
                // llamará a inicioPanel.mostrarDatos() para actualizar los labels.
                estadoCargaLabel.setText("Archivo cargado exitosamente."); // Puedes poner un mensaje provisional aquí
            } catch (IOException ex) {
                // Manejo de errores de IO (archivo no encontrado, permisos, etc.)
                estadoCargaLabel.setText("Error de lectura del archivo: " + ex.getMessage());
                cantidadItemsLabel.setText("Cantidad de ítems: -");
                tiempoTotalLabel.setText("Tiempo estimado total: -");
                iniciarPruebaButton.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Error al leer el archivo:\n" + ex.getMessage(), "Error de Archivo", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Para depuración
            } catch (Exception ex) {
                // Captura cualquier otra excepción que pueda ocurrir durante la carga
                estadoCargaLabel.setText("Error inesperado al cargar el archivo: " + ex.getMessage());
                cantidadItemsLabel.setText("Cantidad de ítems: -");
                tiempoTotalLabel.setText("Tiempo estimado total: -");
                iniciarPruebaButton.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado:\n" + ex.getMessage(), "Error Inesperado", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Para depuración
            }
            // La lógica de 'mostrarDatos' y habilitar el botón se maneja a través de la notificación
            // del controlador a VentanaPrincipal, y luego VentanaPrincipal llamando a mostrarDatos().
        }
    }

    public void mostrarDatos(int cantidad, int tiempo) {
        // Este método se llama desde VentanaPrincipal (que es el observador)
        // una vez que el controlador ha cargado los ítems exitosamente.
        cantidadItemsLabel.setText("Cantidad de ítems: " + cantidad);
        if (tiempo >= 60) {
            int minutos = tiempo / 60;
            int segundosRestantes = tiempo % 60;
            if (segundosRestantes > 0) {
                tiempoTotalLabel.setText("Tiempo estimado total: " + minutos + " minutos y " + segundosRestantes + " segundos");
            } else {
                tiempoTotalLabel.setText("Tiempo estimado total: " + minutos + " minutos");
            }
        } else {
            tiempoTotalLabel.setText("Tiempo estimado total: " + tiempo + " segundos");
        }
        iniciarPruebaButton.setEnabled(true);
        estadoCargaLabel.setText("Archivo cargado exitosamente."); // Mensaje final de éxito
    }
}