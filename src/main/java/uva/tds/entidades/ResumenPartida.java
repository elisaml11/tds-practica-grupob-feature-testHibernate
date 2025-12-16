package uva.tds.entidades;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Representa una partida de escoba entre dos jugadores.
 * Permite almacenar y recuperar información sobre la partida,
 * incluyendo los jugadores, la fecha, los puntos y otros detalles relevantes.
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
@Entity
@Table(name = "resumen_partida")
public class ResumenPartida {

    @Id
    @Column(name = "id", nullable = false, length = 100)
    private String id;

    private LocalDate fecha;
    private String jugador1;
    private String jugador2;
    private boolean completa;

    private int puntosJugador1;
    private int puntosJugador2;

    private int escobasJugador1;
    private int escobasJugador2;

    private int orosJugador1;
    private int orosJugador2;

    private int sietesJugador1;
    private int sietesJugador2;

    private boolean guindisJugador1;
    private boolean guindisJugador2;

    private int cartasCapturadasJugador1;
    private int cartasCapturadasJugador2;

    public ResumenPartida() { }
    
    /**
     * Constructor de Partida.
     *
     * @param id          Identificador único de la partida.
     * @param fecha       Fecha en la que se jugó la partida.
     * @param jugador1    Nombre del primer jugador.
     * @param jugador2    Nombre del segundo jugador.
     * @throws IllegalArgumentException si el id es nulo o vacío, si la fecha es nula,
     *                                  o si los nombres de los jugadores son nulos o vacíos.
     */
    public ResumenPartida(String id, LocalDate fecha, String jugador1, String jugador2) {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("El id no puede ser nulo o vacío");
        if (fecha == null) throw new IllegalArgumentException("La fecha no puede ser nula");
        if (jugador1 == null || jugador1.isEmpty()) throw new IllegalArgumentException("Jugador1 inválido");
        if (jugador2 == null || jugador2.isEmpty()) throw new IllegalArgumentException("Jugador2 inválido");

        this.id = id;
        this.fecha = fecha;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
    }

    /**
     * Obtiene el identificador de la partida.
     */
    public String getId() { return id; }

    /**
     * Obtiene la fecha de la partida.
     */
    public LocalDate getFecha() { return this.fecha; }

    /**
     * Obtiene el nombre del primer jugador.
     */
    public String getJugador1() { return this.jugador1; }

    /**
     * Obtiene el nombre del segundo jugador.
     */
    public String getJugador2() { return this.jugador2; }

    /**
     * Obtiene los puntos del primer jugador.
     */
    public int getPuntosJugador1() { return this.puntosJugador1; }

    /**
     * Obtiene los puntos del segundo jugador.
     */
    public int getPuntosJugador2() { return this.puntosJugador2; }

    /**
     * Obtiene las escobas del primer jugador.
     */
    public int getEscobasJugador1() { return this.escobasJugador1; }

    /**
     * Obtiene las escobas del segundo jugador.
     */
    public int getEscobasJugador2() { return this.escobasJugador2; }

    /**
     * Obtiene los oros del primer jugador.
     */
    public int getOrosJugador1() { return this.orosJugador1; }

    /**
     * Obtiene los oros del segundo jugador.
     */
    public int getOrosJugador2() { return this.orosJugador2; }

    /**
     * Obtiene los sietes del primer jugador.
     */
    public int getSietesJugador1() { return this.sietesJugador1; }

    /**
     * Obtiene los sietes del segundo jugador.
     */
    public int getSietesJugador2() { return this.sietesJugador2; }

    /**
     * Indica si el primer jugador tiene guindis.
     */
    public boolean isGuindisJugador1() { return this.guindisJugador1; }

    /**
     * Indica si el segundo jugador tiene guindis.
     */
    public boolean isGuindisJugador2() { return this.guindisJugador2; }

    /**
     * Obtiene las cartas capturadas del primer jugador.
     */
    public int getCartasCapturadasJugador1() { return this.cartasCapturadasJugador1; }

    /**
     * Obtiene las cartas capturadas del segundo jugador.
     */
    public int getCartasCapturadasJugador2() { return this.cartasCapturadasJugador2; }

    /**
     * Indica si la partida está completa.
     */
    public boolean isCompleta() { return this.completa; }


    /**
     * Obtiene el nombre del jugador ganador.
     *
     * @return Nombre del jugador ganador, o null en caso de empate.
     * @throws IllegalStateException si la partida no está completa.
     */
    public String getGanador() {
        if (!isCompleta()) {
            throw new IllegalStateException("No se puede obtener el ganador de una partida incompleta");
        }
        if (puntosJugador1 == puntosJugador2) {
            return null; 
        }
        return (puntosJugador1 > puntosJugador2) ? jugador1 : jugador2;
    }

    /**
     * Establece los resultados de la partida basándose en los jugadores y sus puntos.
     *
     * @param j1      Primer jugador.
     * @param j2      Segundo jugador.
     * @param puntos1 Puntos del primer jugador.
     * @param puntos2 Puntos del segundo jugador.
     * @throws IllegalArgumentException si algún jugador es nulo.
     */
    public void establecerResultados(Jugador j1, Jugador j2, int puntos1, int puntos2) {
        if (j1 == null || j2 == null) throw new IllegalArgumentException("Jugadores no pueden ser nulos");
        this.puntosJugador1 = puntos1;
        this.puntosJugador2 = puntos2;

        this.escobasJugador1 = j1.getEscobas();
        this.escobasJugador2 = j2.getEscobas();

        this.orosJugador1 = j1.contarOros();
        this.orosJugador2 = j2.contarOros();

        this.sietesJugador1 = j1.contarSietes();
        this.sietesJugador2 = j2.contarSietes();

        this.guindisJugador1 = j1.tieneGuindis();
        this.guindisJugador2 = j2.tieneGuindis();

        this.cartasCapturadasJugador1 = j1.getNumeroCartasCapturadas();
        this.cartasCapturadasJugador2 = j2.getNumeroCartasCapturadas();

        marcarComoCompleta();
    }

    /**
     * Marca la partida como completa.
     */
    public void marcarComoCompleta() {
        this.completa = true;
    }


    /**
     * Compara esta partida con otro objeto para determinar si son iguales.
     * @param obj El objeto a comparar con la instancia actual.
     * @return true si ambos objetos son instancias de Partida y comparten el mismo id,
     * fecha y jugadores, false en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ResumenPartida other = (ResumenPartida) obj;
        return this.id.equals(other.getId())
            && this.fecha.equals(other.getFecha())
            && this.jugador1.equals(other.getJugador1())
            && this.jugador2.equals(other.getJugador2());
    }

}