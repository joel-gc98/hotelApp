package com.gui.hotelapp;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

public final class InterpreteComandos {

    public enum Tipo {
        IR_HABITACIONES, IR_RESERVAS, IR_ACCESIBILIDAD,
        AUMENTAR_TEXTO, REDUCIR_TEXTO, TEXTO_NORMAL,
        BUSCAR_HABITACION, HABITACIONES_DISPONIBLES, BUSCAR_RESERVA,
        DESCONOCIDO
    }

    public record Comando(Tipo tipo, String argumento) {}

    private static final Map<String, Integer> NUMEROS = new HashMap<>();
    static {
        String[] hasta29 = {"uno","dos","tres","cuatro","cinco","seis","siete","ocho","nueve","diez",
                "once","doce","trece","catorce","quince","dieciseis","diecisiete","dieciocho",
                "diecinueve","veinte","veintiuno","veintidos","veintitres","veinticuatro",
                "veinticinco","veintiseis","veintisiete","veintiocho","veintinueve"};
        for (int i = 0; i < hasta29.length; i++) NUMEROS.put(hasta29[i], i + 1);
        NUMEROS.put("un", 1); NUMEROS.put("una", 1);
        String[] decenas = {"treinta","cuarenta","cincuenta","sesenta","setenta","ochenta","noventa"};
        for (int i = 0; i < decenas.length; i++) NUMEROS.put(decenas[i], (i + 3) * 10);
        String[] centenas = {"ciento","doscientos","trescientos","cuatrocientos","quinientos",
                "seiscientos","setecientos","ochocientos","novecientos"};
        for (int i = 0; i < centenas.length; i++) NUMEROS.put(centenas[i], (i + 1) * 100);
        NUMEROS.put("cien", 100);
    }

    public static Comando interpretar(String texto) {
        String t = normalizar(texto);
        if (t.isEmpty()) return new Comando(Tipo.DESCONOCIDO, "");

        if (t.contains("habitaciones disponibles") || t.contains("habitaciones libres"))
            return new Comando(Tipo.HABITACIONES_DISPONIBLES, "");

        if (t.startsWith("buscar reserva")) {
            String nombre = t.replaceFirst("^buscar reserva( de| a nombre de)?", "").trim();
            return new Comando(Tipo.BUSCAR_RESERVA, nombre);
        }
        if (t.startsWith("buscar habitacion") || t.startsWith("consultar habitacion")) {
            String resto = t.replaceFirst("^(buscar|consultar) habitacion( numero)?", "").trim();
            return new Comando(Tipo.BUSCAR_HABITACION, numero(resto));
        }

        if (t.matches("(ir a|abrir|mostrar) (la )?(pestana |seccion )?habitaciones"))
            return new Comando(Tipo.IR_HABITACIONES, "");
        if (t.matches("(ir a|abrir|mostrar) (la )?(pestana |seccion )?reservas"))
            return new Comando(Tipo.IR_RESERVAS, "");
        if (t.matches("(ir a|abrir|mostrar) (la )?(pestana |seccion )?accesibilidad"))
            return new Comando(Tipo.IR_ACCESIBILIDAD, "");

        if (t.matches("(aumentar|agrandar) (la )?(letra|texto)|letra mas grande"))
            return new Comando(Tipo.AUMENTAR_TEXTO, "");
        if (t.matches("(reducir|disminuir) (la )?(letra|texto)|letra mas pequena"))
            return new Comando(Tipo.REDUCIR_TEXTO, "");
        if (t.matches("(texto|letra) normal"))
            return new Comando(Tipo.TEXTO_NORMAL, "");

        return new Comando(Tipo.DESCONOCIDO, t);
    }

    /** Minúsculas y sin tildes, para comparar sin depender de cómo escriba Vosk. */
    private static String normalizar(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s.toLowerCase().trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    /** "ciento doce" -> "112"; si ya son cifras, las deja igual. */
    private static String numero(String s) {
        if (s.matches("\\d+")) return s;
        int total = 0;
        boolean alguno = false;
        for (String p : s.split("\\s+")) {
            Integer v = NUMEROS.get(p);
            if (v != null) { total += v; alguno = true; }
        }
        return alguno ? String.valueOf(total) : s;
    }
}