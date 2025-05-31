package cl.unab.inf.sistemaevaluacion.frontend;

import cl.unab.inf.sistemaevaluacion.backend.Controlador;
import cl.unab.inf.sistemaevaluacion.backend.ObservadorEvaluador;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class VentanaPrincipal extends JFrame implements ObservadorEvaluador {
    private final Controlador controlador;
    private final InicioPanel inicioPanel;
    private final PruebaPanel pruebaPanel;
    private final ResultadoPanel resultadoPanel;
    private final RevisarRespuestasPanel revisarPanel;

    private final JPanel contentPane;
    private final CardLayout cardLayout;

    private JToggleButton themeToggleButton;

    public VentanaPrincipal(Controlador controlador) {
        this.controlador = controlador;
        this.controlador.agregarObservador(this);

        setTitle("Sistema de Evaluación");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 720); // Tamaño ideal por defecto
        setMinimumSize(new Dimension(1024, 720)); // Impide achicar
        setLocationRelativeTo(null); // Centrado en pantalla
        setResizable(false); // Fijo para que no se modifique el tamaño

        // --- Tema oscuro por defecto ---
        try {
            FlatDarkLaf.setup();
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Error al cargar tema FlatLaf: " + ex);
        }

        // --- CardLayout principal ---
        cardLayout = new CardLayout();
        contentPane = new JPanel(cardLayout);
        add(contentPane, BorderLayout.CENTER);

        // --- Paneles de la app ---
        inicioPanel = new InicioPanel(controlador);
        pruebaPanel = new PruebaPanel(controlador);
        resultadoPanel = new ResultadoPanel(controlador);
        revisarPanel = new RevisarRespuestasPanel(controlador);

        contentPane.add(inicioPanel, "Inicio");
        contentPane.add(pruebaPanel, "Prueba");
        contentPane.add(resultadoPanel, "Resultado");
        contentPane.add(revisarPanel, "RevisarRespuestas");

        // --- Botón de cambio de tema (sol/luna) ---
        themeToggleButton = new JToggleButton();
        ImageIcon sunIcon = createSunIcon(24, 24);
        ImageIcon moonIcon = createMoonIcon(24, 24);

        themeToggleButton.setIcon(moonIcon);
        themeToggleButton.setSelectedIcon(sunIcon);
        themeToggleButton.setSelected(true);

        Dimension iconSize = new Dimension(36, 36);
        themeToggleButton.setPreferredSize(iconSize);
        themeToggleButton.setMinimumSize(iconSize);
        themeToggleButton.setMaximumSize(iconSize);

        themeToggleButton.addActionListener(e -> {
            boolean isDark = themeToggleButton.isSelected();
            try {
                if (!isDark) {
                    FlatLightLaf.setup();
                } else {
                    FlatDarkLaf.setup();
                }
                SwingUtilities.updateComponentTreeUI(this);
            } catch (Exception ex) {
                System.err.println("Error al cambiar tema: " + ex);
            }
        });

        JPanel topBarPanel = new JPanel(new BorderLayout());
        topBarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonWrapper.add(themeToggleButton);
        topBarPanel.add(buttonWrapper, BorderLayout.EAST);
        add(topBarPanel, BorderLayout.NORTH);

        // --- Acción que se llama cuando se reinicia la evaluación ---
        controlador.setAccionReintentar(() -> {
            pruebaPanel.mostrarItemActual();         // Mostrar la primera pregunta
            cardLayout.show(contentPane, "Prueba");  // Cambiar al panel de prueba
        });

        // Iniciar en el panel de inicio
        cardLayout.show(contentPane, "Inicio");

        setVisible(true);
    }

    // --- Icono de sol ---
    private ImageIcon createSunIcon(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.ORANGE);
        g2d.fillOval(width / 4, height / 4, width / 2, height / 2);

        g2d.setColor(Color.ORANGE.darker());
        int rayLength = width / 6;
        int centerX = width / 2;
        int centerY = height / 2;
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45);
            int x1 = (int) (centerX + (width / 4) * Math.cos(angle));
            int y1 = (int) (centerY + (height / 4) * Math.sin(angle));
            int x2 = (int) (centerX + (width / 4 + rayLength) * Math.cos(angle));
            int y2 = (int) (centerY + (height / 4 + rayLength) * Math.sin(angle));
            g2d.drawLine(x1, y1, x2, y2);
        }

        g2d.dispose();
        return new ImageIcon(image);
    }

    // --- Icono de luna ---
    private ImageIcon createMoonIcon(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillOval(0, 0, width, height);

        g2d.setComposite(AlphaComposite.SrcOut);
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillOval((int) (width * 0.3), (int) (height * 0.05), (int) (width * 0.85), (int) (height * 0.85));

        g2d.dispose();
        return new ImageIcon(image);
    }

    // --- Método del patrón observador ---
    @Override
    public void actualizar() {
        Controlador.Estado estado = controlador.getEstadoActual();

        switch (estado) {
            case INICIO:
                cardLayout.show(contentPane, "Inicio");
                break;
            case PRUEBA:
                cardLayout.show(contentPane, "Prueba");
                pruebaPanel.mostrarItemActual();
                break;
            case RESULTADO:
                resultadoPanel.actualizarResumen();
                cardLayout.show(contentPane, "Resultado");
                break;
            case REVISION:
                revisarPanel.actualizarVista();
                cardLayout.show(contentPane, "RevisarRespuestas");
                break;
        }

        revalidate();
        repaint();
    }

    @Override
    public void datosCargados(int cantidad, int tiempo) {
        inicioPanel.mostrarDatos(cantidad, tiempo);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Controlador controlador = new Controlador();
            new VentanaPrincipal(controlador);
        });
    }
}