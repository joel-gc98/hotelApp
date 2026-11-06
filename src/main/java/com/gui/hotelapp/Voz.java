package com.gui.hotelapp;

import java.io.IOException;

/**
 * Lectura en voz alta de los mensajes de la aplicación (prototipo).
 *
 * Usa el sintetizador del sistema operativo, sin librerías adicionales:
 * Windows (System.Speech de PowerShell), macOS (say) y Linux (espeak).
 * Si el sistema no dispone de voz, la aplicación sigue funcionando con normalidad.
 */
public final class Voz {

    private static final String SCRIPT_WINDOWS =
            "Add-Type -AssemblyName System.Speech; "
            + "$s = New-Object System.Speech.Synthesis.SpeechSynthesizer; "
            + "try { $s.SelectVoiceByHints([System.Speech.Synthesis.VoiceGender]::NotSet, "
            + "[System.Speech.Synthesis.VoiceAge]::NotSet, 0, "
            + "[System.Globalization.CultureInfo]'es-ES') } catch {} ; "
            + "$s.Speak($env:HOTELAPP_TEXTO)";

    private static boolean activa = false;
    private static Process proceso;

    private Voz() {
    }

    public static synchronized void setActiva(boolean valor) {
        activa = valor;
        if (!valor) detener();
    }

    public static synchronized boolean isActiva() {
        return activa;
    }

    /** Lee el texto si la opción está activada; interrumpe la lectura anterior. */
    public static synchronized void leer(String texto) {
        if (!activa || texto == null || texto.isBlank()) return;
        detener();

        // Los símbolos (✔ ✖ ⚠ ●) no se leen: el significado ya está en el texto
        String limpio = texto.replaceAll("[✔✖⚠●]", "")
                .replace('\n', '.')
                .replaceAll("\\s+", " ")
                .trim();
        if (limpio.isEmpty()) return;

        try {
            String sistema = System.getProperty("os.name", "").toLowerCase();
            ProcessBuilder pb;
            if (sistema.contains("win")) {
                pb = new ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive",
                        "-Command", SCRIPT_WINDOWS);
                // El texto viaja en una variable de entorno: así no hay problemas de comillas
                pb.environment().put("HOTELAPP_TEXTO", limpio);
            } else if (sistema.contains("mac")) {
                pb = new ProcessBuilder("say", "--", limpio);
            } else {
                pb = new ProcessBuilder("espeak", "-v", "es", "--", limpio);
            }
            pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            proceso = pb.start();
        } catch (IOException e) {
            System.err.println("Lectura en voz alta no disponible en este sistema: " + e.getMessage());
        }
    }

    public static synchronized void detener() {
        if (proceso != null && proceso.isAlive()) proceso.destroy();
        proceso = null;
    }
}
