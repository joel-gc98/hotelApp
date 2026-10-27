package com.gui.hotelapp;

public class Habitacion {
    private final int numero;
    private final String estado;
    private final int planta;
    private final String tipo;
    private final int capacidad;
    private final double precioNoche;
    private final String vista;
    private final String accesible;

    public Habitacion(int numero, String estado, int planta, String tipo,
                      int capacidad, double precioNoche, String vista, String accesible) {
        this.numero = numero;
        this.estado = estado;
        this.planta = planta;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.precioNoche = precioNoche;
        this.vista = vista;
        this.accesible = accesible;
    }

    public int numero() { return numero; }
    public String estado() { return estado; }
    public int planta() { return planta; }
    public String tipo() { return tipo; }
    public int capacidad() { return capacidad; }
    public double precioNoche() { return precioNoche; }
    public String vista() { return vista; }
    public String accesible() { return accesible; }
}