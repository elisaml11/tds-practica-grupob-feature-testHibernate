package uva.tds.servicios;

import java.util.ArrayList;

import uva.tds.entidades.Jugador;

/**
 * Clase que gestiona los turnos de los jugadores en una ronda.
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class GestorTurno {
    private final ArrayList<Jugador> jugadores;
    private final int turnosPorRonda;
    private int indiceActual;      // índice en jugadores de quien tiene el turno
    private int turnosJugados;     

    /**
     * Constructor de GestorTurno.
     * @param jugadores Lista de jugadores que participan en la ronda.
     * @param turnosPorRonda Número de turnos que se jugarán en la ronda.
     * @throws IllegalArgumentException si la lista de jugadores es nula o tiene menos de 2 jugadores,
     * o si turnosPorRonda es menor o igual a cero.
     */
    public GestorTurno(ArrayList<Jugador> jugadores, int turnosPorRonda) {
        if (jugadores == null) {
            throw new IllegalArgumentException("La lista de jugadores no puede ser nula");
        }
        if (jugadores.size() < 2) {
            throw new IllegalArgumentException("Se requieren al menos 2 jugadores");
        }
        if (turnosPorRonda <= 0) {
            throw new IllegalArgumentException("turnosPorRonda debe ser mayor que cero");
        }
        this.jugadores = new ArrayList<>(jugadores);
        this.turnosPorRonda = turnosPorRonda;
        this.indiceActual = 0;
        this.turnosJugados = 0;
    }

    /**
     * Inicia el gestor de turnos estableciendo el jugador que comienza.
     * Si el jugador no está en la lista, se inicia con el primer jugador.
     * @param primerJugador Jugador que comenzará la ronda.
     */
    public void iniciarCon(Jugador primerJugador) {
        if (primerJugador == null) {
            this.indiceActual = 0;
        } else {
            int idx = this.jugadores.indexOf(primerJugador);
            this.indiceActual = (idx >= 0) ? idx : 0;
        }
        this.turnosJugados = 0;
    }

    /**
     * Establece el jugador actual si está en la lista de jugadores.
     * @param jugador Jugador a establecer como actual.
     */
    public void establecerJugadorActual(Jugador jugador) {
        if (jugador == null) return;
        int idx = this.jugadores.indexOf(jugador);
        if (idx >= 0) this.indiceActual = idx;
    }

    /**
     * Devuelve el jugador que tiene el turno actual.
     * @return Jugador actual.
     */
    public Jugador getJugadorActual() {
        return this.jugadores.get(this.indiceActual);
    }

    /**
     * Cambia el turno al siguiente jugador y aumenta el contador de turnos jugados.
     */
    public void cambiarTurno() {
        this.indiceActual = (this.indiceActual + 1) % this.jugadores.size();
        this.turnosJugados += 1;
    }

    /**
     * Indica si la ronda ha terminado según el número de turnos jugados.
     * @return true si la ronda ha terminado, false en caso contrario.
     */
    public boolean rondaTerminada() {
        return this.turnosJugados >= this.turnosPorRonda;
    }

    /**
     * Reinicia el contador de turnos jugados a cero.
     */
    public void reiniciarTurnos() {
        this.turnosJugados = 0;
    }

    /**
     * Devuelve el número de turnos jugados hasta el momento.
     * @return Número de turnos jugados.
     */
    public int getTurnosJugados() {
        return this.turnosJugados;
    }

    /**
     * Devuelve una copia de la lista de jugadores.
     * @return Lista de jugadores.
     */
    public ArrayList<Jugador> getJugadores() {
        return new ArrayList<>(this.jugadores);
    }
}
