package cl.unab.inf.sistemaevaluacion.backend;

import java.util.List;

public class ItemSeleccionMultiple extends Item {
    private List<String> opciones;
    private int indiceRespuestaCorrecta;

    public ItemSeleccionMultiple(Tipo tipo, String nivel, String enunciado, List<String> opciones, String respuestaCorrecta, int tiempoEstimado) {
        super(tipo, nivel, enunciado, opciones, respuestaCorrecta, tiempoEstimado);
    }

    public List<String> getOpciones() {
        return opciones;
    }

    public int getIndiceRespuestaCorrecta() {
        return indiceRespuestaCorrecta;
    }

    @Override
    public boolean esCorrecta(String respuesta) {
        try {
            int seleccion = Integer.parseInt(respuesta);
            return seleccion == indiceRespuestaCorrecta;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
