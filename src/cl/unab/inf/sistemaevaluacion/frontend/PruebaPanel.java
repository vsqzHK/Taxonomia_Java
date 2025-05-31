package cl.unab.inf.sistemaevaluacion.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Enumeration;

import cl.unab.inf.sistemaevaluacion.backend.Controlador;
import cl.unab.inf.sistemaevaluacion.backend.Item;

public class PruebaPanel extends JPanel {
    private final Controlador controlador;

    private JLabel enunciadoLabel;
    private JPanel opcionesPanel;
    private ButtonGroup opcionesGroup;
    private JButton anteriorButton;
    private JButton siguienteButton;

    public PruebaPanel(Controlador controlador) {
        this.controlador = controlador;

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        setBackground(new Color(45, 45, 45));

        // Panel superior (enunciado)
        JPanel enunciadoContainerPanel = new JPanel(new BorderLayout());
        enunciadoContainerPanel.setBackground(getBackground());
        enunciadoContainerPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        enunciadoLabel = new JLabel();
        enunciadoLabel.setFont(new Font("Arial", Font.BOLD, 22));
        enunciadoLabel.setForeground(Color.WHITE);
        enunciadoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        enunciadoLabel.setVerticalAlignment(SwingConstants.TOP);

        JPanel enunciadoWrapper = new JPanel(new BorderLayout());
        enunciadoWrapper.setBackground(getBackground());
        enunciadoWrapper.add(enunciadoLabel, BorderLayout.CENTER);

        enunciadoContainerPanel.add(enunciadoWrapper, BorderLayout.CENTER);
        add(enunciadoContainerPanel, BorderLayout.NORTH);

        // Panel central (opciones)
        opcionesPanel = new JPanel();
        opcionesPanel.setLayout(new BoxLayout(opcionesPanel, BoxLayout.Y_AXIS));
        opcionesPanel.setBackground(getBackground());
        opcionesPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPaneOpciones = new JScrollPane(opcionesPanel);
        scrollPaneOpciones.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPaneOpciones.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneOpciones.setPreferredSize(new Dimension(800, 300));
        scrollPaneOpciones.setBackground(getBackground());
        scrollPaneOpciones.getViewport().setBackground(getBackground());

        opcionesGroup = new ButtonGroup();
        add(scrollPaneOpciones, BorderLayout.CENTER);

        // Panel inferior (botones)
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        botonesPanel.setBackground(getBackground());

        anteriorButton = new JButton("Anterior");
        siguienteButton = new JButton("Siguiente");

        anteriorButton.setFont(new Font("Arial", Font.BOLD, 15));
        siguienteButton.setFont(new Font("Arial", Font.BOLD, 15));
        anteriorButton.setFocusPainted(false);
        siguienteButton.setFocusPainted(false);

        anteriorButton.setBackground(new Color(80, 80, 80));
        siguienteButton.setBackground(new Color(60, 130, 200));
        anteriorButton.setForeground(Color.WHITE);
        siguienteButton.setForeground(Color.WHITE);

        botonesPanel.add(anteriorButton);
        botonesPanel.add(siguienteButton);
        add(botonesPanel, BorderLayout.SOUTH);

        anteriorButton.addActionListener(e -> {
            controlador.responderActual(getRespuestaSeleccionada());
            controlador.retroceder();
        });

        siguienteButton.addActionListener(e -> {
            controlador.responderActual(getRespuestaSeleccionada());
            controlador.avanzar();
        });
    }

    public void mostrarItemActual() {
        Item itemActual = controlador.getItemActual();
        if (itemActual != null) {
            enunciadoLabel.setText(
                    "<html><div style='text-align: center; max-width: 900px;'>"
                            + itemActual.getEnunciado()
                            + "</div></html>"
            );

            opcionesPanel.removeAll();
            opcionesGroup = new ButtonGroup();

            for (String opcion : itemActual.getOpciones()) {
                JRadioButton radioButton = new JRadioButton(opcion);
                radioButton.setFont(new Font("Arial", Font.PLAIN, 16));
                radioButton.setForeground(Color.WHITE);
                radioButton.setBackground(new Color(60, 60, 60));
                radioButton.setFocusPainted(false);
                radioButton.setOpaque(true);
                opcionesGroup.add(radioButton);
                opcionesPanel.add(Box.createVerticalStrut(10));
                opcionesPanel.add(radioButton);

                String respuestaUsuario = controlador.getRespuestaUsuarioActual();
                if (respuestaUsuario != null && respuestaUsuario.equals(opcion)) {
                    radioButton.setSelected(true);
                }
            }

            opcionesPanel.revalidate();
            opcionesPanel.repaint();
            actualizarBotonesNavegacion();
        } else {
            enunciadoLabel.setText("No hay preguntas disponibles.");
            opcionesPanel.removeAll();
            opcionesPanel.revalidate();
            opcionesPanel.repaint();
            anteriorButton.setEnabled(false);
            siguienteButton.setEnabled(false);
        }
    }

    private String getRespuestaSeleccionada() {
        for (Enumeration<AbstractButton> buttons = opcionesGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }

    private void actualizarBotonesNavegacion() {
        // CAMBIO AQUÍ: Usamos estaPrimeraPregunta() en lugar de estaEnPrimero()
        anteriorButton.setEnabled(!controlador.estaPrimeraPregunta());
        // CAMBIO AQUÍ: Usamos estaUltimaPregunta() en lugar de estaEnUltimo()
        if (controlador.estaUltimaPregunta()) {
            siguienteButton.setText("Enviar respuestas");
        } else {
            siguienteButton.setText("Siguiente");
        }
    }

    public void actualizarVista() {
        mostrarItemActual();
    }
}