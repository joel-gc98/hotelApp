package com.gui.hotelapp;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

public class HotelRepository {

    public static List<Habitacion> cargarHabitaciones() throws Exception {
        Document doc = abrirXml();
        NodeList nodos = doc.getElementsByTagName("habitacion");
        List<Habitacion> lista = new ArrayList<>();

        for (int i = 0; i < nodos.getLength(); i++) {
            Element h = (Element) nodos.item(i);
            lista.add(new Habitacion(
                    Integer.parseInt(h.getAttribute("numero")),
                    h.getAttribute("estado"),
                    Integer.parseInt(h.getAttribute("planta")),
                    texto(h, "tipo"),
                    Integer.parseInt(texto(h, "capacidad")),
                    Double.parseDouble(texto(h, "precioNoche")),
                    texto(h, "vista"),
                    texto(h, "accesible")));
        }
        return lista;
    }

    public static List<Reserva> cargarReservas() throws Exception {
        Document doc = abrirXml();
        NodeList nodos = doc.getElementsByTagName("reserva");
        List<Reserva> lista = new ArrayList<>();

        for (int i = 0; i < nodos.getLength(); i++) {
            Element r = (Element) nodos.item(i);
            lista.add(new Reserva(
                    r.getAttribute("codigo"),
                    r.getAttribute("estado"),
                    texto(r, "nombre"),
                    texto(r, "apellidos"),
                    texto(r, "email"),
                    Integer.parseInt(texto(r, "habitacionReservada")),
                    LocalDate.parse(texto(r, "entrada")),
                    LocalDate.parse(texto(r, "salida")),
                    Integer.parseInt(texto(r, "personas")),
                    textoOpcional(r, "observaciones")));
        }
        return lista;
    }

    private static Document abrirXml() throws Exception {
        InputStream in = HotelRepository.class.getResourceAsStream("/reservas.xml");
        if (in == null) throw new IllegalStateException("No se encontró reservas.xml en resources");
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
    }

    private static String texto(Element e, String tag) {
        return e.getElementsByTagName(tag).item(0).getTextContent().trim();
    }

    private static String textoOpcional(Element e, String tag) {
        NodeList nodos = e.getElementsByTagName(tag);
        return nodos.getLength() == 0 ? "" : nodos.item(0).getTextContent().trim();
    }
}
