package cl.unab.inf.sistemaevaluacion.backend;

import java.util.List;

public class Item {
    public enum Tipo {
        SELECCION_MULTIPLE,
        VERDADERO_FALSO
    }

    private final Tipo tipo;
    private final String nivel;
    private final String enunciado;
    private final List<String> opciones;
    private final String respuestaCorrecta;
    private final int tiempoEstimado;
    private String explicacion; // Nueva propiedad explicacion

    public Item(Tipo tipo, String nivel, String enunciado, List<String> opciones, String respuestaCorrecta, int tiempoEstimado) {
        this.tipo = tipo;
        this.nivel = nivel;
        this.enunciado = enunciado;
        this.opciones = opciones;
        this.respuestaCorrecta = respuestaCorrecta;
        this.tiempoEstimado = tiempoEstimado;
        this.explicacion = ""; // Inicialización por defecto
    }

    // Getters
    public Tipo getTipo() {
        return tipo;
    }

    public String getNivel() {
        return nivel;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public List<String> getOpciones() {
        return opciones;
    }

    public String getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public int getTiempoEstimado() {
        return tiempoEstimado;
    }

    public String getExplicacion() {
        return explicacion;
    }

    public void setExplicacion(String explicacion) {
        this.explicacion = explicacion;
    }

    public boolean esCorrecta(String respuestaUsuario) {
        if (respuestaUsuario == null) {
            return false;
        }
        return respuestaCorrecta.trim().equalsIgnoreCase(respuestaUsuario.trim());
    }

    public String getTipoComoTexto() {
        switch (tipo) {
            case SELECCION_MULTIPLE:
                return "Seleccion Multiple";
            case VERDADERO_FALSO:
                return "Verdadero/Falso";
            default:
                return "Desconocido";
        }
    }
}