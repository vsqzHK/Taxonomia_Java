package cl.unab.inf.sistemaevaluacion.backend;

import java.util.*;
import java.io.*;
import java.util.stream.Collectors; // Se importa por si se usa en el futuro, no es estrictamente necesario para este código

public class Controlador {

    public enum Estado {
        INICIO, PRUEBA, RESULTADO, REVISION
    }

    private Estado estadoActual;
    private final List<Item> items = new ArrayList<>();
    private final List<String> respuestasUsuario = new ArrayList<>();
    private int indiceActual = 0;
    private final List<ObservadorEvaluador> observadores = new ArrayList<>();
    private Evaluador evaluador;
    private Runnable accionReintentar; // Acción personalizada para reiniciar

    public static class DetalleRespuesta {
        public Item item;
        public String respuestaUsuario;
        public boolean esCorrecta;

        public DetalleRespuesta(Item item, String respuestaUsuario, boolean esCorrecta) {
            this.item = item;
            this.respuestaUsuario = respuestaUsuario;
            this.esCorrecta = esCorrecta;
        }
    }

    public Controlador() {
        this.estadoActual = Estado.INICIO;
    }

    public Controlador(ObservadorEvaluador observador) {
        this();
        this.suscribir(observador);
    }

    public Controlador(Evaluador evaluador) {
        this();
        this.evaluador = evaluador;
    }

    public void setAccionReintentar(Runnable accion) {
        this.accionReintentar = accion;
    }

    public void reiniciarEvaluacion() {
        // Reiniciar las respuestas del usuario a null para una nueva prueba
        // Asegurarse de que respuestasUsuario tenga el tamaño correcto si los items cambiaron
        // (Aunque en este caso, se asume que los items no cambian hasta una nueva carga de archivo)
        Collections.fill(respuestasUsuario, null);
        indiceActual = 0;
        estadoActual = Estado.PRUEBA;
        if (accionReintentar != null) {
            accionReintentar.run(); // Ejecuta la acción de la UI para actualizar la vista del panel de prueba
        }
        notificarObservadores(); // Notifica a todos los observadores (principalmente VentanaPrincipal) del cambio de estado
    }

    public void suscribir(ObservadorEvaluador o) {
        observadores.add(o);
    }

    public void agregarObservador(ObservadorEvaluador o) {
        suscribir(o);
    }

    private void notificarObservadores() {
        for (ObservadorEvaluador o : observadores) {
            o.actualizar(); // Llama a actualizar en todos los observadores
        }
    }

    public void cargarItemsDesdeArchivo(File archivo) throws IOException {
        items.clear(); // Limpia cualquier ítem cargado previamente
        respuestasUsuario.clear(); // Limpia respuestas anteriores

        int lineasProcesadas = 0;
        int itemsCargadosExitosamente = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            System.out.println("Iniciando carga de ítems desde: " + archivo.getAbsolutePath());
            while ((linea = br.readLine()) != null) {
                lineasProcesadas++;
                // Ignorar líneas vacías o que solo contienen espacios
                if (linea.trim().isEmpty()) {
                    System.out.println("Línea " + lineasProcesadas + ": Ignorando línea vacía.");
                    continue;
                }

                String[] partes = linea.split(";");
                // Verificar que haya al menos 6 partes (tipo, nivel, enunciado, opciones, respuesta, tiempo)
                if (partes.length < 6) {
                    System.err.println("Error en línea " + lineasProcesadas + ": Formato incorrecto (se esperaban al menos 6 partes separadas por ';'). Línea: \"" + linea + "\"");
                    continue; // Saltar esta línea y continuar con la siguiente
                }

                try {
                    Item.Tipo tipo = Item.Tipo.valueOf(partes[0].trim().toUpperCase());
                    String nivel = partes[1].trim();
                    String enunciado = partes[2].trim();

                    // Las opciones se separan por '|', hay que escapar el carácter de la barra
                    List<String> opciones = Arrays.asList(partes[3].split("\\|"));
                    if (opciones.isEmpty() || (opciones.size() == 1 && opciones.get(0).trim().isEmpty())) {
                        System.err.println("Error en línea " + lineasProcesadas + ": Opciones vacías o mal formadas. Línea: \"" + linea + "\"");
                        continue;
                    }

                    String respuestaCorrecta = partes[4].trim();
                    int tiempo = Integer.parseInt(partes[5].trim());

                    Item item = new Item(tipo, nivel, enunciado, opciones, respuestaCorrecta, tiempo);

                    if (partes.length >= 7) {
                        item.setExplicacion(partes[6].trim());
                    }

                    items.add(item);
                    respuestasUsuario.add(null); // Añadir un espacio para la respuesta de este ítem
                    itemsCargadosExitosamente++;
                    System.out.println("Línea " + lineasProcesadas + ": Ítem cargado: " + enunciado.substring(0, Math.min(enunciado.length(), 50)) + "..."); // Mostrar parte del enunciado
                } catch (IllegalArgumentException e) {
                    System.err.println("Error en línea " + lineasProcesadas + ": Tipo de ítem, número o formato de opciones inválido. Detalles: " + e.getMessage() + ". Línea: \"" + linea + "\"");
                    // Posibles errores: Item.Tipo.valueOf falla, Integer.parseInt falla
                    continue;
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Error en línea " + lineasProcesadas + ": Índice fuera de límites al parsear partes. Detalles: " + e.getMessage() + ". Línea: \"" + linea + "\"");
                    // Posible si partes[x] no existe, aunque la primera verificación de partes.length ayuda
                    continue;
                } catch (Exception e) { // Captura cualquier otra excepción inesperada
                    System.err.println("Error inesperado en línea " + lineasProcesadas + ": " + e.getMessage() + ". Línea: \"" + linea + "\"");
                    continue;
                }
            }
            System.out.println("Carga de ítems finalizada. " + itemsCargadosExitosamente + " ítems cargados exitosamente de " + lineasProcesadas + " líneas procesadas.");
        } // Fin del try-with-resources (BufferedReader)

        // Después de intentar cargar todos los ítems, notificar a los observadores
        estadoActual = Estado.INICIO; // Asegurarse de que el estado sea el inicial de la aplicación
        notificarObservadores(); // Notifica a los observadores sobre el cambio de estado

        // ---- ¡ESTE ES EL CAMBIO CLAVE PARA TU PROBLEMA ACTUAL! ----
        // Llama a datosCargados en cada observador para que InicioPanel actualice sus labels.
        for (ObservadorEvaluador o : observadores) {
            o.datosCargados(obtenerCantidadItems(), obtenerTiempoTotal());
        }

        // Si no se cargó ningún ítem, podrías querer lanzar una excepción o notificar de otra forma
        if (items.isEmpty()) {
            throw new IOException("No se pudo cargar ningún ítem del archivo. Verifique el formato y contenido.");
        }
    }

    public int obtenerCantidadItems() {
        return items.size();
    }

    public int getCantidadItems() { // Este es un alias para obtenerCantidadItems
        return obtenerCantidadItems();
    }

    public int obtenerTiempoTotal() {
        return items.stream().mapToInt(Item::getTiempoEstimado).sum();
    }

    public int getTiempoTotal() { // Este es un alias para obtenerTiempoTotal
        return obtenerTiempoTotal();
    }

    public void iniciarPrueba() {
        if (items.isEmpty()) {
            System.err.println("No se puede iniciar la prueba: No hay ítems cargados.");
            // Opcional: Notificar a la UI con un JOptionPane
            // JOptionPane.showMessageDialog(null, "No hay preguntas cargadas para iniciar la prueba.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        estadoActual = Estado.PRUEBA;
        indiceActual = 0;
        notificarObservadores(); // Notifica a la UI que cambie al panel de prueba
    }

    public boolean estaEnModoPrueba() {
        return estadoActual == Estado.PRUEBA;
    }

    public boolean estaEnModoResultado() {
        return estadoActual == Estado.RESULTADO;
    }

    public boolean estaEnModoRevisar() {
        return estadoActual == Estado.REVISION;
    }

    public Item getItemActual() {
        if (items.isEmpty() || indiceActual < 0 || indiceActual >= items.size()) {
            return null;
        }
        return items.get(indiceActual);
    }

    public String getRespuestaUsuarioActual() {
        if (indiceActual < 0 || indiceActual >= respuestasUsuario.size()) {
            return null;
        }
        return respuestasUsuario.get(indiceActual);
    }

    public void responderActual(String respuesta) {
        if (indiceActual >= 0 && indiceActual < respuestasUsuario.size()) {
            respuestasUsuario.set(indiceActual, respuesta);
        }
    }

    public void avanzar() {
        if (indiceActual < items.size() - 1) {
            indiceActual++;
            notificarObservadores();
        } else {
            estadoActual = Estado.RESULTADO; // Transición al estado de resultado al final
            notificarObservadores();
        }
    }

    public void retroceder() {
        if (indiceActual > 0) {
            indiceActual--;
            notificarObservadores();
        }
    }

    public void siguiente() {
        avanzar();
    }

    public void anterior() {
        retroceder();
    }

    public boolean estaPrimeraPregunta() {
        return indiceActual == 0;
    }

    public boolean estaUltimaPregunta() {
        return !items.isEmpty() && indiceActual == items.size() - 1;
    }

    public int getRespuestasCorrectasCount() {
        int correctCount = 0;
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String respuestaUsuario = respuestasUsuario.get(i);
            if (item.esCorrecta(respuestaUsuario)) {
                correctCount++;
            }
        }
        return correctCount;
    }

    public int getRespuestasIncorrectasCount() {
        return items.size() - getRespuestasCorrectasCount();
    }

    public List<DetalleRespuesta> getDetalleRespuestas() {
        List<DetalleRespuesta> detalles = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String respuestaUsuario = respuestasUsuario.get(i);
            boolean esCorrecta = item.esCorrecta(respuestaUsuario);
            detalles.add(new DetalleRespuesta(item, respuestaUsuario, esCorrecta));
        }
        return detalles;
    }

    public Map<String, Double> getResumenPorNivel() {
        Map<String, Integer> conteoCorrectas = new HashMap<>();
        Map<String, Integer> totalPorNivel = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String nivel = item.getNivel();

            totalPorNivel.put(nivel, totalPorNivel.getOrDefault(nivel, 0) + 1);

            String respuestaUsuario = respuestasUsuario.get(i);
            if (item.esCorrecta(respuestaUsuario)) {
                conteoCorrectas.put(nivel, conteoCorrectas.getOrDefault(nivel, 0) + 1);
            }
        }

        Map<String, Double> porcentaje = new HashMap<>();
        for (String nivel : totalPorNivel.keySet()) {
            int correctas = conteoCorrectas.getOrDefault(nivel, 0);
            int total = totalPorNivel.get(nivel);
            double resultado = total > 0 ? (100.0 * correctas / total) : 0.0;
            porcentaje.put(nivel, resultado);
        }
        return porcentaje;
    }

    public Map<Item.Tipo, Double> getResumenPorTipo() {
        Map<Item.Tipo, Integer> conteoCorrectas = new HashMap<>();
        Map<Item.Tipo, Integer> totalPorTipo = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            Item.Tipo tipo = item.getTipo();

            totalPorTipo.put(tipo, totalPorTipo.getOrDefault(tipo, 0) + 1);

            String respuestaUsuario = respuestasUsuario.get(i);
            if (item.esCorrecta(respuestaUsuario)) {
                conteoCorrectas.put(tipo, conteoCorrectas.getOrDefault(tipo, 0) + 1);
            }
        }

        Map<Item.Tipo, Double> porcentaje = new HashMap<>();
        for (Item.Tipo tipo : totalPorTipo.keySet()) {
            int correctas = conteoCorrectas.getOrDefault(tipo, 0);
            int total = totalPorTipo.get(tipo);
            double resultado = total > 0 ? (100.0 * correctas / total) : 0.0;
            porcentaje.put(tipo, resultado);
        }
        return porcentaje;
    }

    public void revisarRespuestas() {
        estadoActual = Estado.REVISION;
        indiceActual = 0;
        notificarObservadores();
    }

    public void volverAlResumen() {
        estadoActual = Estado.RESULTADO;
        notificarObservadores();
    }

    public boolean respuestaEsCorrecta() {
        if (getItemActual() == null) {
            return false;
        }
        String respuestaUsuario = getRespuestaUsuarioActual();
        return getItemActual().esCorrecta(respuestaUsuario);
    }

    public void enviarRespuestas() {
        notificarFinalizado();
    }

    public void notificarFinalizado() {
        estadoActual = Estado.RESULTADO;
        notificarObservadores();
    }

    public int getIndiceActual() {
        return indiceActual;
    }

    public List<String> getRespuestasUsuario() {
        return respuestasUsuario;
    }

    public Estado getEstadoActual() {
        return estadoActual;
    }

    public List<Item> getItems() {
        return items;
    }
}