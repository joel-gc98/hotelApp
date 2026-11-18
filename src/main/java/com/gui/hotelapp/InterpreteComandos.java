package com.gui.hotelapp;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class InterpreteComandos {

    public enum Tipo {
        IR_HABITACIONES, IR_RESERVAS, IR_ACCESIBILIDAD,
        BUSCAR_HABITACION, HABITACIONES_DISPONIBLES, MOSTRAR_TODAS_HABITACIONES,
        BUSCAR_RESERVA, MOSTRAR_TODAS_RESERVAS,
        AUMENTAR_TEXTO, REDUCIR_TEXTO, TEXTO_NORMAL,
        TEMA_CLARO, TEMA_OSCURO, TEMA_ALTO_CONTRASTE,
        LECTURA_ON, LECTURA_OFF, TECLADO_ON, TECLADO_OFF,
        DESCONOCIDO
    }

    public record Comando(Tipo tipo, String argumento) {}

    private record Regla(Pattern patron, Tipo tipo) {}

    private static Regla regla(String patron, Tipo tipo) {
        return new Regla(Pattern.compile(patron), tipo);
    }

    // Texto ya normalizado (minúsculas, sin tildes). Solo el argumento va entre paréntesis de captura.
    private static final List<Regla> REGLAS = List.of(
            regla(".*habitaciones (?:disponibles|libres)", Tipo.HABITACIONES_DISPONIBLES),
            regla("(?:mostrar|ver) todas las habitaciones", Tipo.MOSTRAR_TODAS_HABITACIONES),
            regla("(?:mostrar|ver) todas las reservas", Tipo.MOSTRAR_TODAS_RESERVAS),
            regla("buscar reserva(?: de| a nombre de)? (.+)", Tipo.BUSCAR_RESERVA),
            regla("(?:buscar|consultar) habitacion(?: numero)? (.+)", Tipo.BUSCAR_HABITACION),

            regla("(?:ir a|abrir|mostrar) (?:la )?(?:pestana |seccion )?habitaciones", Tipo.IR_HABITACIONES),
            regla("(?:ir a|abrir|mostrar) (?:la )?(?:pestana |seccion )?reservas", Tipo.IR_RESERVAS),
            regla("(?:ir a|abrir|mostrar) (?:la )?(?:pestana |seccion )?accesibilidad", Tipo.IR_ACCESIBILIDAD),

            regla("(?:aumentar|agrandar) (?:la )?(?:letra|texto)|letra mas grande", Tipo.AUMENTAR_TEXTO),
            regla("(?:reducir|disminuir) (?:la )?(?:letra|texto)|letra mas pequena", Tipo.REDUCIR_TEXTO),
            regla("(?:texto|letra) normal", Tipo.TEXTO_NORMAL),

            regla("(?:activar |poner )?(?:tema|modo) claro", Tipo.TEMA_CLARO),
            regla("(?:activar |poner )?(?:tema|modo) oscuro", Tipo.TEMA_OSCURO),
            regla("(?:activar |poner )?(?:(?:tema|modo) )?alto contraste", Tipo.TEMA_ALTO_CONTRASTE),

            regla("activar (?:la )?lectura(?: en voz alta)?", Tipo.LECTURA_ON),
            regla("desactivar (?:la )?lectura(?: en voz alta)?", Tipo.LECTURA_OFF),
            regla("activar (?:la )?navegacion por teclado", Tipo.TECLADO_ON),
            regla("desactivar (?:la )?navegacion por teclado", Tipo.TECLADO_OFF)
    );

    private static final Map<String, Integer> NUMEROS = new HashMap<>();
    static {
        String[] hasta29 = {"uno","dos","tres","cuatro","cinco","seis","siete","ocho","nueve","diez",
                "once","doce","trece","catorce","quince","dieciseis","diecisiete","dieciocho",
                "diecinueve","veinte","veintiuno","veintidos","veintitres","veinticuatro",
                "veinticinco","veintiseis","veintisiete","veintiocho","veintinueve"};
        for (int i = 0; i < hasta29.length; i++) NUMEROS.put(hasta29[i], i + 1);
        NUMEROS.put("un", 1);
        NUMEROS.put("una", 1);
        String[] decenas = {"treinta","cuarenta","cincuenta","sesenta","setenta","ochenta","noventa"};
        for (int i = 0; i < decenas.length; i++) NUMEROS.put(decenas[i], (i + 3) * 10);
        String[] centenas = {"ciento","doscientos","trescientos","cuatrocientos","quinientos",
                "seiscientos","setecientos","ochocientos","novecientos"};
        for (int i = 0; i < centenas.length; i++) NUMEROS.put(centenas[i], (i + 1) * 100);
        NUMEROS.put("cien", 100);
    }

    private InterpreteComandos() {}

    public static Comando interpretar(String texto) {
        String t = normalizar(texto);
        for (Regla r : REGLAS) {
            Matcher m = r.patron().matcher(t);
            if (m.matches()) {
                String arg = m.groupCount() > 0 ? m.group(1).trim() : "";
                return new Comando(r.tipo(), r.tipo() == Tipo.BUSCAR_HABITACION ? numero(arg) : arg);
            }
        }
        return new Comando(Tipo.DESCONOCIDO, t);
    }

    /** Minúsculas y sin tildes, para comparar sin depender de cómo escriba Vosk. */
    public static String normalizar(String s) {
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
            if (v != null) {
                total += v;
                alguno = true;
            }
        }
        return alguno ? String.valueOf(total) : s;
    }
}