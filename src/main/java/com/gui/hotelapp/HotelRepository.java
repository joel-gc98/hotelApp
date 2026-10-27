package com.gui.hotelapp;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.*;

public class HotelRepository {

    public static List<Habitacion> cargarHabitaciones() throws Exception {
        InputStream in = HotelRepository.class.getResourceAsStream("/reservas.xml");
        if (in == null) throw new IllegalStateException("No se encontró reservas.xml en resources");

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
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

    private static String texto(Element e, String tag) {
        return e.getElementsByTagName(tag).item(0).getTextContent().trim();
    }
}