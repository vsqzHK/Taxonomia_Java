package cl.unab.inf.sistemaevaluacion.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;
import cl.unab.inf.sistemaevaluacion.backend.Controlador;
import cl.unab.inf.sistemaevaluacion.backend.Item;

public class ResultadoPanel extends JPanel {
    private final Controlador controlador;

    private JLabel tituloResultadosLabel;
    private JLabel correctasLabel;
    private JLabel incorrectasLabel;
    private JPanel panelResumenBloom;
    private JPanel panelResumenTipo;
    private JTextArea detalleRespuestasArea;
    private JButton revisarRespuestasButton;
    private JButton btnReintentar;

    public ResultadoPanel(Controlador controlador) {
        this.controlador = controlador;

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPanel.setBackground(new Color(45, 45, 45));
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel envolvente que mantiene el centrado horizontal
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setBackground(new Color(45, 45, 45));
        wrapper.add(contentPanel);

        JScrollPane mainScrollPane = new JScrollPane(wrapper);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setLayout(new BorderLayout());
        add(mainScrollPane, BorderLayout.CENTER);

        tituloResultadosLabel = new JLabel("Resultados de la Prueba");
        tituloResultadosLabel.setFont(new Font("Arial", Font.BOLD, 28));
        tituloResultadosLabel.setForeground(Color.WHITE);
        tituloResultadosLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        correctasLabel = new JLabel("Respuestas Correctas: -");
        correctasLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        correctasLabel.setForeground(Color.LIGHT_GRAY);
        correctasLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        incorrectasLabel = new JLabel("Respuestas Incorrectas: -");
        incorrectasLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        incorrectasLabel.setForeground(Color.LIGHT_GRAY);
        incorrectasLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel bloomTitle = new JLabel("Resumen por Nivel de Bloom");
        bloomTitle.setFont(new Font("Arial", Font.BOLD, 16));
        bloomTitle.setForeground(Color.WHITE);
        bloomTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelResumenBloom = new JPanel(new GridLayout(0, 1, 5, 8));
        panelResumenBloom.setBackground(getBackground());
        panelResumenBloom.setMaximumSize(new Dimension(500, 999));
        panelResumenBloom.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tipoTitle = new JLabel("Resumen por Tipo de Ítem");
        tipoTitle.setFont(new Font("Arial", Font.BOLD, 16));
        tipoTitle.setForeground(Color.WHITE);
        tipoTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelResumenTipo = new JPanel(new GridLayout(0, 1, 5, 8));
        panelResumenTipo.setBackground(getBackground());
        panelResumenTipo.setMaximumSize(new Dimension(500, 999));
        panelResumenTipo.setAlignmentX(Component.CENTER_ALIGNMENT);

        detalleRespuestasArea = new JTextArea(10, 80);
        detalleRespuestasArea.setFont(new Font("Arial", Font.PLAIN, 14));
        detalleRespuestasArea.setEditable(false);
        detalleRespuestasArea.setLineWrap(true);
        detalleRespuestasArea.setWrapStyleWord(true);
        detalleRespuestasArea.setBackground(new Color(60, 60, 60));
        detalleRespuestasArea.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(detalleRespuestasArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                "Detalle de Respuestas Incorrectas:",
                0, 0, new Font("Arial", Font.BOLD, 14), Color.LIGHT_GRAY
        ));
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        revisarRespuestasButton = new JButton("Revisar Respuestas");
        revisarRespuestasButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        revisarRespuestasButton.setFont(new Font("Arial", Font.BOLD, 14));
        revisarRespuestasButton.setBackground(new Color(60, 130, 200));
        revisarRespuestasButton.setForeground(Color.WHITE);
        revisarRespuestasButton.setFocusPainted(false);
        revisarRespuestasButton.addActionListener((ActionEvent e) -> controlador.revisarRespuestas());

        btnReintentar = new JButton("Reintentar evaluación");
        btnReintentar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReintentar.setFont(new Font("Arial", Font.BOLD, 14));
        btnReintentar.setBackground(new Color(80, 80, 80));
        btnReintentar.setForeground(Color.WHITE);
        btnReintentar.setFocusPainted(false);
        btnReintentar.addActionListener((ActionEvent e) -> controlador.reiniciarEvaluacion());

        contentPanel.add(tituloResultadosLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(correctasLabel);
        contentPanel.add(incorrectasLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(bloomTitle);
        contentPanel.add(panelResumenBloom);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(tipoTitle);
        contentPanel.add(panelResumenTipo);
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(revisarRespuestasButton);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(btnReintentar);
        contentPanel.add(Box.createVerticalStrut(20));
    }

    public void actualizarResumen() {
        int correctas = controlador.getRespuestasCorrectasCount();
        int incorrectas = controlador.getRespuestasIncorrectasCount();

        correctasLabel.setText("Respuestas Correctas: " + correctas);
        incorrectasLabel.setText("Respuestas Incorrectas: " + incorrectas);

        panelResumenBloom.removeAll();
        for (Map.Entry<String, Double> entry : controlador.getResumenPorNivel().entrySet()) {
            String nivel = entry.getKey();
            int valor = (int) Math.round(entry.getValue());
            JProgressBar barra = new JProgressBar(0, 100);
            barra.setValue(valor);
            barra.setString(nivel.toUpperCase() + ": " + valor + "%");
            barra.setStringPainted(true);
            barra.setForeground(colorPorcentaje(valor));
            barra.setPreferredSize(new Dimension(300, 20));
            panelResumenBloom.add(barra);
        }

        panelResumenTipo.removeAll();
        for (Map.Entry<Item.Tipo, Double> entry : controlador.getResumenPorTipo().entrySet()) {
            String tipo = entry.getKey().toString();
            int valor = (int) Math.round(entry.getValue());
            JProgressBar barra = new JProgressBar(0, 100);
            barra.setValue(valor);
            barra.setString(tipo.toUpperCase() + ": " + valor + "%");
            barra.setStringPainted(true);
            barra.setForeground(colorPorcentaje(valor));
            barra.setPreferredSize(new Dimension(300, 20));
            panelResumenTipo.add(barra);
        }

        StringBuilder detalle = new StringBuilder("Respuestas Incorrectas:\n\n");
        int incorrectaIndex = 1;
        boolean hayIncorrectas = false;
        for (Controlador.DetalleRespuesta dr : controlador.getDetalleRespuestas()) {
            if (!dr.esCorrecta) {
                hayIncorrectas = true;
                detalle.append(incorrectaIndex++).append(". ").append(dr.item.getEnunciado()).append("\n");
                detalle.append("   Tu respuesta: ").append(dr.respuestaUsuario != null ? dr.respuestaUsuario : "[No respondida]").append("\n");
                detalle.append("   Correcta: ").append(dr.item.getRespuestaCorrecta()).append("\n");
                if (dr.item.getExplicacion() != null) {
                    detalle.append("   Explicación: ").append(dr.item.getExplicacion()).append("\n");
                }
                detalle.append("\n");
            }
        }

        if (!hayIncorrectas) {
            detalle.append("¡Todas tus respuestas fueron correctas!");
        }

        detalleRespuestasArea.setText(detalle.toString());
        detalleRespuestasArea.setCaretPosition(0);
    }

    private Color colorPorcentaje(int valor) {
        if (valor < 30) return new Color(200, 0, 0);
        if (valor < 50) return new Color(255, 140, 0);
        if (valor < 70) return new Color(255, 215, 0);
        return new Color(0, 153, 0);
    }
}
