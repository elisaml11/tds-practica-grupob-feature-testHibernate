package uva.tds.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Representa una carta de la baraja española de 40 cartas utilizada en el juego
 * de la escoba.
 * Cada carta tiene un palo y un índice que determina su valor.
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
@Entity
@Table(name = "cartas")
public class Carta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "palo", nullable = false, length = 8)
    private Palo palo;

    @Column(name = "indice", nullable = false)
    private int indice;

    public Carta() {
    }

    /**
     * Crea una nueva carta con el palo e índice especificados.
     * 
     * @param palo   el palo de la carta (OROS, COPAS, ESPADAS o BASTOS)
     * @param indice el índice de la carta (1-7 o 10-12)
     * @throws IllegalArgumentException si el palo es nulo
     * @throws IllegalArgumentException si el índice no está en el rango válido (1-7
     *                                  o 10-12)
     */
    public Carta(Palo palo, int indice) {
        if (palo == null) {
            throw new IllegalArgumentException("El palo no puede ser nulo.");
        }
        if (!((indice >= 1 && indice <= 7) || (indice >= 10 && indice <= 12))) {
            throw new IllegalArgumentException("Indice inválido: " + indice);
        }
        this.palo = palo;
        this.indice = indice;
    }

    /**
     * Devuelve el palo de la carta.
     * 
     * @return el palo de la carta
     */
    public Palo getPalo() {
        return this.palo;
    }

    /**
     * Devuelve el índice de la carta.
     * 
     * @return el índice de la carta (1-7 o 10-12)
     */
    public int getIndice() {
        return this.indice;
    }

    /*
     * /**
     * Crea una carta a partir de un string con el formato "índice-palo".
     * Sirve para convertir las cartas leídas desde el fichero JSON.
     * 
     * @param str cadena de la carta a leer
     * 
     * @return la carta creada a partir de la cadena
     * 
     * @throws IllegalArgumentException si la cadena es nula o vacía.
     * 
     * @throws IllegalArgumentException si el formato de la cadena es inválido,
     * indice-palo.
     * 
     * @throws IllegalArgumentException si el índice no es un número de dos cifras.
     * 
     * @throws IllegalArgumentException si el palo no está en el enum Palo.
     * 
     * public static Carta fromString(String str){
     * if (str == null || str.isEmpty()) {
     * throw new IllegalArgumentException("La cadena no puede ser nula o vacía.");
     * }
     * 
     * String[] partes = str.split("-");
     * if (partes.length != 2) {
     * throw new
     * IllegalArgumentException("Formato inválido. Debe ser 'índice-palo'.");
     * }
     * int indice;
     * try {
     * indice=Integer.parseInt(partes[0]);
     * } catch (NumberFormatException e) {
     * throw new IllegalArgumentException("Índice inválido" );
     * }
     * Palo palo;
     * try {
     * palo = Palo.valueOf(partes[1].toUpperCase());
     * } catch (IllegalArgumentException e) {
     * throw new IllegalArgumentException("Palo inválido" );
     * }
     * return new Carta(palo, indice);
     * }
     */

    /**
     * Comprueba si dos cartas son iguales, es decir si tienen mismo palo e indice
     * 
     * @param obj el objeto a comparar con esta carta
     * @return true si es igual, false si no
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Carta other = (Carta) obj;
        return indice == other.indice && palo == other.palo;
    }

    /**
     * Devuelve una representación en texto de la carta en el formato
     * "<índice>-<palo>" donde <palo> está en minúsculas.
     * Ejemplos: "1-oros", "11-espadas".
     *
     * @return cadena con el índice y el palo separados por un guion
     */
    @Override
    public String toString() {
        return indice + "-" + palo.name().toLowerCase();
    }

}
