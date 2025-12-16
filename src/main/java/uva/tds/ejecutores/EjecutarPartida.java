package uva.tds.ejecutores;

import java.util.ArrayList;
import java.util.List;

import uva.tds.entidades.Carta;
import uva.tds.entidades.Jugada;
import uva.tds.entidades.Jugador;
import uva.tds.entidades.Partida;
import uva.tds.entidades.Ronda;
import uva.tds.entidades.Turno;
import uva.tds.servicios.GestorPartida;

/**
 * Clase encargada de ejecutar una partida de Escoba a partir de un objeto
 * partida con todos los datos de esta
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class EjecutarPartida {

    private final Partida partida;
    private GestorPartida gestor;

    public EjecutarPartida(Partida partida) {
        if (partida == null) {
            throw new IllegalArgumentException("La partida no puede ser nula");
        }
        this.partida = partida;
    }

    /**
     * Obtiene la partida a ejecutar.
     * @return La partida a ejecutar.
     */
    public Partida getPartida() {
        return this.partida;
    }

    /**
     * Ejecuta la partida completa aplicando las jugadas indicadas en partida.
     * Devuelve el GestorPartida con el estado final.
     * @return GestorPartida resultante de ejecutar la partida.
     */
    public GestorPartida ejecutarPartidaCompleta() {

        // Validaciones explícitas para lanzar NullPointerException (según esperan los tests)
        if (partida.getNombres().isEmpty()) {
            throw new NullPointerException("La partida no contiene los nombres de los jugadores");
        }
        if (partida.getMesaInicial().isEmpty()) {
            throw new NullPointerException("La partida no contiene la mesa inicial");
        }
        if (partida.getManosJugador1().isEmpty() || partida.getManosJugador2().isEmpty()) {
            throw new NullPointerException("La partida no contiene las manos de los jugadores");
        }
        if (partida.getRondas().isEmpty()) {
            throw new NullPointerException("La partida no contiene las rondas");
        }

        // Ahora sí es seguro acceder a los índices
        Jugador jugador1 = new Jugador(this.partida.getNombres().get(0));
        Jugador jugador2 = new Jugador(this.partida.getNombres().get(1));

        this.gestor = new GestorPartida(jugador1, jugador2);

        ArrayList<Carta> manoJug1 = this.partida.getManosJugador1().get(0);
        ArrayList<Carta> manoJug2 = this.partida.getManosJugador2().get(0);
        ArrayList<Carta> mesaInicial = this.partida.getMesaInicial();
        gestor.repartoInicial(manoJug1, manoJug2, mesaInicial);

        final int TOTAL_RONDAS = 6;
        List<Ronda> rondas = this.partida.getRondas();
        for (int ronda = 0; ronda < TOTAL_RONDAS; ronda++) {
            ejecutarRonda(gestor, rondas.get(ronda));
        }

        gestor.finalizarPartida();

        return this.gestor;
    }

    /**
     * Ejecuta una ronda completa de la partida, incluido el reparto de la siguiente ronda.
     * 
     * @param gestor El gestor que ejecuta la partida.
     * @param nRonda El número de la ronda a ejecutar.
     */
    private void ejecutarRonda(GestorPartida gestor, Ronda ronda) {
        List<Turno> turnos = ronda.getTurnos();
        for (int turno = 0; turno <= 5; turno++) {
            Jugada jug = turnos.get(turno).getJugada();
            Carta cartaJugada = jug.getJuega();
            ArrayList<Carta> cartasCapturadas = new ArrayList<>(jug.getCaptura());

            gestor.jugarCarta(cartaJugada, cartasCapturadas);
        }

        if (ronda.getNumero() < 6) {
            gestor.avanzarRonda();

            ArrayList<Carta> manoJug1 = this.partida.getManosJugador1().get(ronda.getNumero());
            ArrayList<Carta> manoJug2 = this.partida.getManosJugador2().get(ronda.getNumero());
            gestor.repartoRonda(manoJug1, manoJug2);
        }
    }
}