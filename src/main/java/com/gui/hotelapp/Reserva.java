package com.gui.hotelapp;

import java.time.LocalDate;

public class Reserva {
    private final String codigo;
    private final String estado;
    private final String nombre;
    private final String apellidos;
    private final String email;
    private final int habitacion;
    private final LocalDate entrada;
    private final LocalDate salida;
    private final int personas;
    private final String observaciones;

    public Reserva(String codigo, String estado, String nombre, String apellidos, String email,
                   int habitacion, LocalDate entrada, LocalDate salida, int personas,
                   String observaciones) {
        this.codigo = codigo;
        this.estado = estado;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.habitacion = habitacion;
        this.entrada = entrada;
        this.salida = salida;
        this.personas = personas;
        this.observaciones = observaciones;
    }

    public String codigo() { return codigo; }
    public String estado() { return estado; }
    public String nombre() { return nombre; }
    public String apellidos() { return apellidos; }
    public String email() { return email; }
    public int habitacion() { return habitacion; }
    public LocalDate entrada() { return entrada; }
    public LocalDate salida() { return salida; }
    public int personas() { return personas; }
    public String observaciones() { return observaciones; }

    public String nombreCompleto() { return nombre + " " + apellidos; }
}
