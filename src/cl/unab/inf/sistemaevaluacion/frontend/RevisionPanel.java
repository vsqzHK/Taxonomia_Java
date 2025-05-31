package cl.unab.inf.sistemaevaluacion.frontend;

import cl.unab.inf.sistemaevaluacion.backend.Controlador;
import cl.unab.inf.sistemaevaluacion.backend.Item;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class RevisionPanel extends JPanel {
    private final Controlador controlador;
    private final JLabel resumenNivelLabel;
    private final JLabel resumenTipoLabel;
    private final JButton revisarRespuestasButton;

    public RevisionPanel(Controlador controlador) {
        this.controlador = controlador;

        resumenNivelLabel = new JLabel();
        resumenTipoLabel = new JLabel();
        revisarRespuestasButton = new JButton("Revisar Respuestas");

        revisarRespuestasButton.addActionListener(e -> controlador.revisarRespuestas());

        configurarLayout();
        actualizarResumen(); // Se actualiza inmediatamente al mostrar el panel
    }

    private void configurarLayout() {
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel titulo = new JLabel("Resumen de Resultados");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(titulo)
                        .addComponent(resumenNivelLabel)
                        .addComponent(resumenTipoLabel)
                        .addComponent(revisarRespuestasButton)
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(titulo)
                        .addComponent(resumenNivelLabel)
                        .addComponent(resumenTipoLabel)
                        .addGap(20)
                        .addComponent(revisarRespuestasButton)
        );
    }

    private void actualizarResumen() {
        Map<String, Double> resumenPorNivel = controlador.getResumenPorNivel();
        Map<Item.Tipo, Double> resumenPorTipo = controlador.getResumenPorTipo();

        StringBuilder nivelBuilder = new StringBuilder("<html><b>Correctas por Nivel:</b><br>");
        for (Map.Entry<String, Double> entry : resumenPorNivel.entrySet()) {
            nivelBuilder.append(entry.getKey()).append(": ")
                    .append(String.format("%.2f%%", entry.getValue())).append("<br>");
        }
        nivelBuilder.append("</html>");

        StringBuilder tipoBuilder = new StringBuilder("<html><b>Correctas por Tipo:</b><br>");
        for (Map.Entry<Item.Tipo, Double> entry : resumenPorTipo.entrySet()) {
            tipoBuilder.append(entry.getKey()).append(": ")
                    .append(String.format("%.2f%%", entry.getValue())).append("<br>");
        }
        tipoBuilder.append("</html>");

        resumenNivelLabel.setText(nivelBuilder.toString());
        resumenTipoLabel.setText(tipoBuilder.toString());
    }
}