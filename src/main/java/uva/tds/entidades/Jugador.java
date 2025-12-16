package uva.tds.entidades;

import java.util.ArrayList;

/**
 * Clase que representa a un jugador en el juego de la escoba.
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */

public class Jugador {
    
    private String nombre;
    private ArrayList<Carta> mano = new ArrayList<>();
    private ArrayList<Carta> cartas = new ArrayList<>();
    private int escobas;

    /**
     * Constructor de la clase Jugador.
     * 
     * @param nombre Nombre del jugador.
     * @throws IllegalArgumentException Si el nombre es nulo.
     * @throws IllegalArgumentException Si el nombre es vacio.
     */
    public Jugador(String nombre){

        if(nombre == null){
            throw new IllegalArgumentException("El nombre no puede ser nulo");
        }
        if(nombre.isEmpty()){
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }        
        this.nombre = nombre;
        this.mano = new ArrayList<>();
        this.cartas = new ArrayList<>();
        this.escobas = 0;
    }

    /**
     * Obtiene el nombre del jugador.
     * @return Nombre del jugador.
     */
    public String getNombre(){
        return this.nombre;
    }

    /**
     * Obtiene la mano del jugador.
     * @return Copia de las carta en la mano del jugador.
     */
    public ArrayList<Carta> getMano(){
        return new ArrayList<>(this.mano);
    }

    /**
     * Obtiene las cartas capturadas por el jugador.
     * @return Copia de las cartas capturadas por el jugador.
     */
    public ArrayList<Carta> getCartas(){
        return new ArrayList<>(this.cartas);
    }

    /**
     * Agrega una carta a la mano del jugador.
     * @param carta Carta a agregar.
     * @throws IllegalArgumentException Si la carta es nula.
     */
    public void agregarCartaAMano(Carta carta){
        if(carta == null){
            throw new IllegalArgumentException("La carta no puede ser nula");
        }
        this.mano.add(carta);

    }

    /**
     * Agrega una carta a las cartas capturadas por el jugador.
     * @param carta Carta a agregar.
     * @throws IllegalArgumentException Si la carta es nula.
     */
    public void agregarCartaACartas(Carta carta){
        if(carta == null){
            throw new IllegalArgumentException("La carta no puede ser nula");
        }
        this.cartas.add(carta);
    }

    /**
     * Elimina una carta de la mano del jugador.
     * @param carta Carta a eliminar.
     * @throws IllegalArgumentException Si la carta es nula.
     * @throws IllegalArgumentException Si la carta no esta en la mano del jugador.
     */
    public void tirarCarta(Carta carta){
        if(carta == null){
            throw new IllegalArgumentException("La carta no puede ser nula");
        }
        if(!this.mano.contains(carta)){
            throw new IllegalArgumentException("La carta no esta en la mano del jugador");
        }
        this.mano.remove(carta);
    }

    /**
     * Incrementa el numero de escobas del jugador en 1.
     */
    public void sumarEscoba(){
        this.escobas += 1;
    }

    /**
     * Obtiene el numero de escobas del jugador.
     * @return Numero de escobas del jugador.
     */
    public int getEscobas(){
        return this.escobas;
    }

    /**
     * Cuenta el numero de sietes en las cartas capturadas por el jugador.
     * @return Numero de sietes en las cartas capturadas por el jugador.
     */
    public int contarSietes() {
        int sietes = 0;
        for(Carta carta : this.cartas) {
            if(carta.getIndice() == 7) {
                sietes++;
            }
        }
        return sietes;
    }

    /**
     * Cuenta el numero de oros en las cartas capturadas por el jugador.
     * @return Numero de oros en las cartas capturadas por el jugador.
     */
    public int contarOros() {
        int oros = 0;
        for(Carta carta : this.cartas) {
            if(carta.getPalo() == Palo.OROS) {
                oros++;
            }
        }
        return oros;
    }

    /**
     * Comprueba si el jugador tiene el siete de oros entre sus cartas capturadas.
     * @return true si el jugador tiene el siete de oros, false si no lo tiene.
     */
    public boolean tieneGuindis() {
        return this.cartas.contains(new Carta(Palo.OROS, 7));
    }

    /**
     * Obtiene el numero de cartas capturadas por el jugador.
     * @return Numero de cartas capturadas por el jugador.
     */
    public int getNumeroCartasCapturadas() {
        return this.cartas.size();
    }


}
