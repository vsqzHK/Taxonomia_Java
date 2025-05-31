package cl.unab.inf.sistemaevaluacion.frontend;

import cl.unab.inf.sistemaevaluacion.backend.Controlador;
import cl.unab.inf.sistemaevaluacion.backend.Item;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class RevisarRespuestasPanel extends JPanel {
    private final Controlador controlador;
    private final JLabel lblEnunciado;
    private final ButtonGroup grupoOpciones;
    private final JPanel panelOpciones;
    private final JLabel lblResultado;
    private final JLabel lblExplicacion;
    private final JButton btnAnterior;
    private final JButton btnSiguiente;
    private final JButton btnVolverResumen;

    public RevisarRespuestasPanel(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
        setBackground(new Color(45, 45, 45));

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        contenido.setBackground(new Color(45, 45, 45));

        // ----- ENUNCIADO -----
        lblEnunciado = new JLabel();
        lblEnunciado.setFont(new Font("Arial", Font.BOLD, 22));
        lblEnunciado.setForeground(Color.WHITE);
        lblEnunciado.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel panelEnunciado = new JPanel(new BorderLayout());
        panelEnunciado.setBackground(new Color(45, 45, 45));
        panelEnunciado.add(lblEnunciado, BorderLayout.CENTER);

        // ----- OPCIONES -----
        grupoOpciones = new ButtonGroup();
        panelOpciones = new JPanel(new GridBagLayout());
        panelOpciones.setBackground(new Color(45, 45, 45));

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Opciones de Respuesta:"
        );
        border.setTitleFont(new Font("Arial", Font.BOLD, 14));
        border.setTitleColor(Color.LIGHT_GRAY);
        panelOpciones.setBorder(border);

        JScrollPane scrollOpciones = new JScrollPane(panelOpciones);
        scrollOpciones.setPreferredSize(new Dimension(10, 250));
        scrollOpciones.setBackground(new Color(45, 45, 45));
        scrollOpciones.setBorder(null);
        scrollOpciones.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollOpciones.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // ----- RESULTADO -----
        lblResultado = new JLabel();
        lblResultado.setFont(new Font("Arial", Font.BOLD, 18));
        lblResultado.setForeground(Color.WHITE);
        lblResultado.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ----- EXPLICACION -----
        lblExplicacion = new JLabel();
        lblExplicacion.setFont(new Font("Arial", Font.PLAIN, 16));
        lblExplicacion.setForeground(Color.LIGHT_GRAY);
        lblExplicacion.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel panelExplicacion = new JPanel(new BorderLayout());
        panelExplicacion.setBackground(new Color(45, 45, 45));
        panelExplicacion.add(lblExplicacion, BorderLayout.CENTER);

        // ----- BOTONES -----
        btnAnterior = new JButton("Anterior");
        btnSiguiente = new JButton("Siguiente");
        btnVolverResumen = new JButton("Volver al resumen");

        for (JButton btn : new JButton[]{btnAnterior, btnSiguiente, btnVolverResumen}) {
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setBackground(new Color(65, 65, 65));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
        }

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        botones.setBackground(new Color(45, 45, 45));
        botones.add(btnAnterior);
        botones.add(btnSiguiente);
        botones.add(btnVolverResumen);

        // ----- AGREGAR TODO -----
        contenido.add(panelEnunciado);
        contenido.add(Box.createVerticalStrut(10));
        contenido.add(scrollOpciones);
        contenido.add(Box.createVerticalStrut(15));
        contenido.add(lblResultado);
        contenido.add(Box.createVerticalStrut(5));
        contenido.add(panelExplicacion);
        contenido.add(Box.createVerticalStrut(20));
        contenido.add(botones);

        add(contenido, BorderLayout.CENTER);
        configurarEventos();
    }

    private void configurarEventos() {
        btnAnterior.addActionListener((ActionEvent e) -> controlador.anterior());
        btnSiguiente.addActionListener((ActionEvent e) -> {
            if (controlador.estaUltimaPregunta()) {
                controlador.notificarFinalizado();
            } else {
                controlador.siguiente();
            }
        });
        btnVolverResumen.addActionListener((ActionEvent e) -> controlador.notificarFinalizado());
    }

    public void actualizarVista() {
        Item item = controlador.getItemActual();
        List<String> opciones = item.getOpciones();
        String respuestaCorrecta = item.getRespuestaCorrecta();
        String respuestaUsuario = controlador.getRespuestaUsuarioActual();

        lblEnunciado.setText("<html><div style='text-align: center; max-width: 900px;'>" + item.getEnunciado() + "</div></html>");

        panelOpciones.removeAll();
        grupoOpciones.clearSelection();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(4, 10, 4, 10);

        for (String opcion : opciones) {
            JRadioButton radio = new JRadioButton(opcion);
            radio.setEnabled(false);
            radio.setFont(new Font("Arial", Font.PLAIN, 16));
            radio.setForeground(Color.LIGHT_GRAY);
            if (opcion.equals(respuestaUsuario)) {
                radio.setSelected(true);
            }
            grupoOpciones.add(radio);
            panelOpciones.add(radio, gbc);
        }

        boolean esCorrecta = respuestaUsuario != null && respuestaUsuario.equals(respuestaCorrecta);
        lblResultado.setText(esCorrecta ? "Correcta" : "Incorrecta. Respuesta Correcta: " + respuestaCorrecta);
        lblResultado.setForeground(esCorrecta ? new Color(0, 200, 0) : new Color(200, 0, 0));

        String explicacion = item.getExplicacion();
        lblExplicacion.setText(explicacion != null && !explicacion.isEmpty()
                ? "<html><div style='text-align: center; max-width: 900px;'>" + explicacion + "</div></html>"
                : "");

        btnAnterior.setEnabled(!controlador.estaPrimeraPregunta());
        btnSiguiente.setText(controlador.estaUltimaPregunta() ? "Volver al resumen" : "Siguiente");

        revalidate();
        repaint();
    }
}
