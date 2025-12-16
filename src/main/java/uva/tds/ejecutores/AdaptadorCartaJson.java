package uva.tds.ejecutores;

import uva.tds.entidades.Carta;
import uva.tds.entidades.Palo;

/**
 * Clase de adaptación entre la representación externa de una carta
 * (la cadena que viene en el JSON) y la entidad Carta.
 *
 * Si el formato del JSON cambia, modificar sólo este método.
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public final class AdaptadorCartaJson {

    private AdaptadorCartaJson() { }

    /**
     * Parsea una cadena con el formato "índice-palo" y devuelve la carta correspondiente.
     * @param strCarta string de entrada
     * @return Carta representada por la cadena
     * @throws IllegalArgumentException si la cadena no tiene el formato correcto
     */
    public static Carta parse(String strCarta) {
        if (strCarta == null) {
            throw new IllegalArgumentException("La cadena de carta no puede ser nula");
        }
        String s = strCarta.trim();
        if (s.isEmpty()) {
            throw new IllegalArgumentException("La cadena de carta no puede estar vacía");
        }

        String normalized = s.replace(':', '-').replace(' ', '-');

        String[] partes = normalized.split("-");
        if (partes.length != 2) {
            throw new IllegalArgumentException("Formato de carta inválido: " + strCarta);
        }

        String indiceStr = partes[0].trim();
        String paloStr = partes[1].trim().toUpperCase();

        int indice;
        try {
            indice = Integer.parseInt(indiceStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Índice de carta inválido: " + indiceStr, e);
        }

        Palo palo;
        try {
            palo = Palo.valueOf(paloStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Palo inválido: " + paloStr, e);
        }

        return new Carta(palo, indice);
    }
}