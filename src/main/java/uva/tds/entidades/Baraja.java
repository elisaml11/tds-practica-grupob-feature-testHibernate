package uva.tds.entidades;

import java.util.ArrayList;

/**
 * Representa una baraja española de 40 cartas utilizada en el juego de la escoba.
 * La baraja contiene 4 palos (OROS, COPAS, ESPADAS, BASTOS) con 10 cartas cada uno. 
 * Los índices válidos son del 1 al 7 y del 10 al 12 (excluyendo 8 y 9).
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class Baraja {

    private ArrayList<Carta> cartas;

    /**
     * Crea una nueva baraja española de 40 cartas.
     * Inicializa la baraja con todas las cartas de los 4 palos,
     * con índices del 1 al 7 y del 10 al 12 para cada palo.
     */
    public Baraja() {
        cartas = new ArrayList<>();
        for (Palo palo : Palo.values()) {
            for (int i = 1; i <= 7; i++) {
                cartas.add(new Carta(palo, i));
            }
            for (int i = 10; i <= 12; i++) {
                cartas.add(new Carta(palo, i));
            }
        }
    }

    /**
     * Devuelve el número de cartas que quedan en la baraja.
     * 
     * @return el número de cartas restantes en la baraja
     */
    public int getNumeroDeCartas() {
        return cartas.size();
    }

    /**
     * Devuelve una copia de la lista de cartas de la baraja.
     * 
     * @return una copia de la lista de cartas de la baraja
     */
    public ArrayList<Carta> getCartas() {
        return new ArrayList<>(cartas);
    }

    /**
     * Roba (elimina) una carta determinada de la baraja.
     * 
     * @param cartaRobar carta que se quiere robar
     * @throws IllegalStateException si el mazo no tiene cartas
     * @throws IllegalArgumentException si la carta a robar no puede ser nula
     * @throws IllegalArgumentException si la carta a robar no está en la baraja
     */
    public void robarCarta(Carta cartaRobar) {
        if(this.cartas.isEmpty()){
            throw new IllegalStateException("No hay cartas en la baraja.");  
        }
        if (cartaRobar == null) {
            throw new IllegalArgumentException("La carta a robar no puede ser nula.");
        }
        if (!cartas.contains(cartaRobar)) {
            throw new IllegalArgumentException("La carta a robar no está en la baraja.");
        }
        cartas.remove(cartaRobar);
    }

}
