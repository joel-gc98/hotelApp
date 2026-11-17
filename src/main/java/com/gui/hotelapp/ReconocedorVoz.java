package com.gui.hotelapp;

import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Entrada por voz: captura el micrófono y devuelve el texto reconocido con Vosk (local). */
public final class ReconocedorVoz {

    private static final Pattern TEXTO = Pattern.compile("\"text\"\\s*:\\s*\"([^\"]*)\"");
    private static Model modelo;   // se carga una sola vez

    private ReconocedorVoz() {}

    private static synchronized Model modelo() throws Exception {
        if (modelo == null) {
            modelo = new Model(Path.of("modelos", "vosk-model-small-es-0.42").toString());
        }
        return modelo;
    }

    /** Bloquea unos segundos: llamar siempre desde un Task, nunca desde el hilo de JavaFX. */
    public static String escuchar(int segundos) throws Exception {
        Voz.detener(); // que el micrófono no capte la voz sintetizada de la app

        AudioFormat formato = new AudioFormat(16000f, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, formato);
        if (!AudioSystem.isLineSupported(info)) {
            throw new IllegalStateException("No hay micrófono compatible.");
        }

        try (Recognizer rec = new Recognizer(modelo(), 16000);
             TargetDataLine mic = (TargetDataLine) AudioSystem.getLine(info)) {
            mic.open(formato);
            mic.start();
            byte[] buffer = new byte[4096];
            long fin = System.currentTimeMillis() + segundos * 1000L;
            while (System.currentTimeMillis() < fin) {
                int n = mic.read(buffer, 0, buffer.length);
                if (n > 0) rec.acceptWaveForm(buffer, n);
            }
            mic.stop();
            Matcher m = TEXTO.matcher(rec.getFinalResult());
            return m.find() ? m.group(1).trim() : "";
        }
    }
}