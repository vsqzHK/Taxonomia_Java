package cl.unab.inf.sistemaevaluacion;

import cl.unab.inf.sistemaevaluacion.backend.*;
import cl.unab.inf.sistemaevaluacion.frontend.*;

public class Main {
    public static void main(String[] args) {
        Evaluador evaluador = new Evaluador();
        Controlador controlador = new Controlador(evaluador);
        VentanaPrincipal ventana = new VentanaPrincipal(controlador);
        controlador.agregarObservador(ventana);
        ventana.setVisible(true);
    }
}