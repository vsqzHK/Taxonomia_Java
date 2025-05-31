package cl.unab.inf.sistemaevaluacion.backend;

public interface ObservadorEvaluador {
    void actualizar();
    void datosCargados(int cantidad, int tiempo); // <-- ¡Asegúrate de que esta línea esté presente!
}