package cl.unab.inf.sistemaevaluacion.backend;

import java.util.Collections;

public class ItemVerdaderoFalso extends Item {

    public ItemVerdaderoFalso(String nivel, String enunciado, String respuestaCorrecta, int tiempoEstimado) {
        super(Tipo.VERDADERO_FALSO, nivel, enunciado, Collections.emptyList(), respuestaCorrecta, tiempoEstimado);
    }

    @Override
    public boolean esCorrecta(String respuesta) {
        // Aceptamos respuestas tipo "true", "false", "verdadero", "falso"
        String respuestaNormalizada = respuesta.trim().toLowerCase();
        String correctaNormalizada = getRespuestaCorrecta().trim().toLowerCase();

        return (
                (respuestaNormalizada.equals("true") || respuestaNormalizada.equals("verdadero")) &&
                        (correctaNormalizada.equals("true") || correctaNormalizada.equals("verdadero"))
        ) || (
                (respuestaNormalizada.equals("false") || respuestaNormalizada.equals("falso")) &&
                        (correctaNormalizada.equals("false") || correctaNormalizada.equals("falso"))
        );
    }
}