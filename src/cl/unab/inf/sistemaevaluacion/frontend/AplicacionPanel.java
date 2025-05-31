package cl.unab.inf.sistemaevaluacion.frontend;

import cl.unab.inf.sistemaevaluacion.backend.Controlador;
import cl.unab.inf.sistemaevaluacion.backend.Item;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Enumeration;

public class AplicacionPanel extends JPanel {
    private final Controlador controlador;

    private final JLabel enunciadoLabel = new JLabel();
    private final JPanel opcionesPanel = new JPanel();
    private final JButton botonAnterior = new JButton("Atrás");
    private final JButton botonSiguiente = new JButton("Siguiente");
    private final ButtonGroup grupoOpciones = new ButtonGroup();

    public AplicacionPanel(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        enunciadoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(enunciadoLabel, BorderLayout.CENTER);

        opcionesPanel.setLayout(new BoxLayout(opcionesPanel, BoxLayout.Y_AXIS));

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botonesPanel.add(botonAnterior);
        botonesPanel.add(botonSiguiente);

        add(topPanel, BorderLayout.NORTH);
        add(opcionesPanel, BorderLayout.CENTER);
        add(botonesPanel, BorderLayout.SOUTH);

        botonAnterior.addActionListener(e -> {
            guardarRespuesta();
            // CAMBIO: Usar controlador.anterior() en lugar de controlador.anteriorItem()
            controlador.anterior();
            cargarItemActual();
        });

        botonSiguiente.addActionListener(e -> {
            guardarRespuesta();
            // CAMBIO: Usar controlador.estaUltimaPregunta() en lugar de controlador.esUltimoItem()
            if (controlador.estaUltimaPregunta()) {
                controlador.enviarRespuestas();
            } else {
                // CAMBIO: Usar controlador.siguiente() en lugar de controlador.siguienteItem()
                controlador.siguiente();
                cargarItemActual();
            }
        });

        cargarItemActual();
    }

    public void cargarItemActual() { // Hacemos este método público para que VentanaPrincipal lo pueda llamar
        // CAMBIO: Usar controlador.getItemActual() en lugar de controlador.obtenerItemActual()
        Item item = controlador.getItemActual();
        if (item == null) {
            enunciadoLabel.setText("No hay preguntas disponibles.");
            opcionesPanel.removeAll();
            opcionesPanel.revalidate();
            opcionesPanel.repaint();
            botonAnterior.setEnabled(false);
            botonSiguiente.setEnabled(false);
            return;
        }

        enunciadoLabel.setText("<html><body style='width: 700px'>" + item.getEnunciado() + "</body></html>");
        opcionesPanel.removeAll();
        grupoOpciones.clearSelection();

        List<String> opciones = item.getOpciones();
        for (String opcion : opciones) {
            JRadioButton botonOpcion = new JRadioButton(opcion);
            botonOpcion.setFont(new Font("Arial", Font.PLAIN, 14));
            grupoOpciones.add(botonOpcion);
            opcionesPanel.add(botonOpcion);

            // Marcar si ya fue respondido
            // CAMBIO: Usar controlador.getRespuestaUsuarioActual() en lugar de controlador.obtenerRespuestaUsuarioActual()
            String respuestaUsuario = controlador.getRespuestaUsuarioActual();
            if (respuestaUsuario != null && respuestaUsuario.equals(opcion)) {
                botonOpcion.setSelected(true);
            }
        }

        // Esta línea ya estaba correcta, ya que estaPrimeraPregunta() existe en Controlador
        botonAnterior.setEnabled(!controlador.estaPrimeraPregunta());

        // CAMBIO: Usar controlador.estaUltimaPregunta() en lugar de controlador.esUltimoItem()
        if (controlador.estaUltimaPregunta()) {
            botonSiguiente.setText("Enviar respuestas");
        } else {
            botonSiguiente.setText("Siguiente");
        }

        revalidate();
        repaint();
    }

    private void guardarRespuesta() {
        String respuestaSeleccionada = null;
        Enumeration<AbstractButton> botones = grupoOpciones.getElements();
        while (botones.hasMoreElements()) {
            AbstractButton boton = botones.nextElement();
            if (boton.isSelected()) {
                respuestaSeleccionada = boton.getText();
                break;
            }
        }

        // CAMBIO: Usar controlador.responderActual() en lugar de controlador.registrarRespuesta()
        controlador.responderActual(respuestaSeleccionada);
    }
}