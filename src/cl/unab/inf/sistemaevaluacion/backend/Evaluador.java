package cl.unab.inf.sistemaevaluacion.backend;

import java.util.*;

public class Evaluador {
    private List<Item> items = new ArrayList<>();
    private Map<Integer, String> respuestasUsuario = new HashMap<>();
    private int indiceActual = 0;
    private boolean pruebaFinalizada = false;

    public void setItems(List<Item> items) {
        this.items = items;
        this.indiceActual = 0;
        this.pruebaFinalizada = false;
        this.respuestasUsuario.clear();
    }

    public int getTotalItems() {
        return items.size();
    }

    public int getTiempoTotalEstimado() {
        return items.stream().mapToInt(Item::getTiempoEstimado).sum();
    }

    public Item getItemActual() {
        if (items.isEmpty()) return null;
        return items.get(indiceActual);
    }

    public int getIndiceActual() {
        return indiceActual;
    }

    public void siguiente() {
        if (indiceActual < items.size() - 1) {
            indiceActual++;
        }
    }

    public void anterior() {
        if (indiceActual > 0) {
            indiceActual--;
        }
    }

    public void responderActual(String respuesta) {
        respuestasUsuario.put(indiceActual, respuesta);
    }

    public String getRespuestaUsuario(int index) {
        return respuestasUsuario.getOrDefault(index, "");
    }

    public String getRespuestaUsuarioActual() {
        return getRespuestaUsuario(indiceActual);
    }

    public boolean estaFinalizada() {
        return pruebaFinalizada;
    }

    public void finalizarPrueba() {
        this.pruebaFinalizada = true;
    }

    // ----------------- Revisión de resultados -----------------

    public Map<String, Double> obtenerPorcentajePorNivelBloom() {
        Map<String, Integer> totalesPorNivel = new HashMap<>();
        Map<String, Integer> correctasPorNivel = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String nivel = item.getNivel();
            totalesPorNivel.put(nivel, totalesPorNivel.getOrDefault(nivel, 0) + 1);

            if (item.getRespuestaCorrecta().equalsIgnoreCase(respuestasUsuario.getOrDefault(i, ""))) {
                correctasPorNivel.put(nivel, correctasPorNivel.getOrDefault(nivel, 0) + 1);
            }
        }

        Map<String, Double> porcentajes = new HashMap<>();
        for (String nivel : totalesPorNivel.keySet()) {
            int total = totalesPorNivel.get(nivel);
            int correctas = correctasPorNivel.getOrDefault(nivel, 0);
            porcentajes.put(nivel, 100.0 * correctas / total);
        }
        return porcentajes;
    }

    public Map<Item.Tipo, Double> obtenerPorcentajePorTipoItem() {
        Map<Item.Tipo, Integer> totalesPorTipo = new HashMap<>();
        Map<Item.Tipo, Integer> correctasPorTipo = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            Item.Tipo tipo = item.getTipo();
            totalesPorTipo.put(tipo, totalesPorTipo.getOrDefault(tipo, 0) + 1);

            if (item.getRespuestaCorrecta().equalsIgnoreCase(respuestasUsuario.getOrDefault(i, ""))) {
                correctasPorTipo.put(tipo, correctasPorTipo.getOrDefault(tipo, 0) + 1);
            }
        }

        Map<Item.Tipo, Double> porcentajes = new HashMap<>();
        for (Item.Tipo tipo : totalesPorTipo.keySet()) {
            int total = totalesPorTipo.get(tipo);
            int correctas = correctasPorTipo.getOrDefault(tipo, 0);
            porcentajes.put(tipo, 100.0 * correctas / total);
        }
        return porcentajes;
    }

    public boolean fueRespondidaCorrectamente(int index) {
        Item item = items.get(index);
        String respuesta = respuestasUsuario.getOrDefault(index, "");
        return item.getRespuestaCorrecta().equalsIgnoreCase(respuesta);
    }
}