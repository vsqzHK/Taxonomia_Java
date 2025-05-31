package cl.unab.inf.sistemaevaluacion.backend;

public class RespuestaUsuario {
    private Item item;
    private String respuesta;

    public RespuestaUsuario(Item item, String respuesta) {
        this.item = item;
        this.respuesta = respuesta;
    }

    public Item getItem() {
        return item;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public boolean esCorrecta() {
        return item.esCorrecta(respuesta);
    }

    // Devuelve el tipo como String legible para análisis (ej. "Seleccion Multiple", "Verdadero/Falso")
    public String getTipoItem() {
        return item.getTipoComoTexto(); // usa el método helper definido en Item
    }

    public String getNivel() {
        return item.getNivel(); // nombre correcto del método
    }
}