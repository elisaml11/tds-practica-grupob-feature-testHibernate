package uva.tds.entidades;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Representa un turno en una partida de cartas.
 * Cada turno tiene un número identificador y una jugada asociada.
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
@Entity
@Table(name = "turnos")
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int numero;
    // si creas, eliminas o actualizas un Turno, se hace lo mismo con la Jugada asociada
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER) // cuando cargas un Turno, tambien se carga automática/ la Jugada
    @JoinColumn(name = "jugada_id", referencedColumnName = "id")
    private Jugada jugada;

    @ManyToOne(fetch = FetchType.LAZY) // cuando cargas un Turno, no se carga automatica/ la Ronda
    @JoinColumn(name = "ronda_id")
    private Ronda ronda;

    public Turno() { }
    /**
     * Construye un turno con el número y la jugada especificados.
     * 
     * @param numero El número del turno (debe ser >= 1).
     * @param jugada La jugada realizada en este turno (no puede ser nula).
     * @throws IllegalArgumentException Si el número es menor que 1 o la jugada es nula.
     */
    public Turno(int numero, Jugada jugada) {
        if (numero < 1) throw new IllegalArgumentException("El número de turno debe ser >= 1");
        if (jugada == null) throw new IllegalArgumentException("La jugada no puede ser nula");
        this.numero = numero;
        this.jugada = jugada;
    }

    /**
     * Devuelve el número del turno.
     * 
     * @return El número del turno.
     */
    public int getNumero() {
        return numero;
    }

    /**
     * Devuelve la jugada realizada en este turno.
     * 
     * @return La jugada del turno.
     */
    public Jugada getJugada() {
        return jugada;
    }
}