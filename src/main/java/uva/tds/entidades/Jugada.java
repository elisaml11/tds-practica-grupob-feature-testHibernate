package uva.tds.entidades;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Clase que representa una jugada en una partida de cartas.
 * Cada jugada tiene una carta que se juega, una lista de cartas capturadas y
 * una lista de cartas en la mesa resultante.
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
@Entity
@Table(name = "jugadas")
public class Jugada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // carta que se juega
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "carta_juega_id", referencedColumnName = "id")
    private Carta juega;

    // cartas capturadas (tabla intermedia)
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "jugada_captura", joinColumns = { @JoinColumn(name = "jugada_id") }, inverseJoinColumns = {
            @JoinColumn(name = "carta_id") })
    private List<Carta> captura = new ArrayList<>();

    // cartas en la mesa resultante (tabla intermedia)
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "jugada_mesa_resultante", joinColumns = {
            @JoinColumn(name = "jugada_id") }, inverseJoinColumns = { @JoinColumn(name = "carta_id") })
    private List<Carta> mesaResultante = new ArrayList<>();

    public Jugada() {
    }

    /**
     * Construye una jugada con la carta que se juega, la lista de cartas capturadas
     * y la lista de cartas en la mesa resultante.
     * 
     * @param juega          La carta que se juega (no puede ser nula).
     * @param captura        La lista de cartas capturadas (si es nula, se considera
     *                       vacía).
     * @param mesaResultante La lista de cartas en la mesa resultante (si es nula,
     *                       se considera vacía).
     * @throws IllegalArgumentException Si la carta que se juega es nula.
     */
    public Jugada(Carta juega, List<Carta> captura, List<Carta> mesaResultante) {
        if (juega == null)
            throw new IllegalArgumentException("La carta que juega no puede ser nula");
        this.juega = juega;
        if (captura == null) {
            this.captura = new ArrayList<>();
        } else {
            this.captura = new ArrayList<>(captura);
        }

        if (mesaResultante == null) {
            this.mesaResultante = new ArrayList<>();
        } else {
            this.mesaResultante = new ArrayList<>(mesaResultante);
        }
    }

    /**
     * Devuelve la carta que se juega en esta jugada.
     * 
     * @return La carta que se juega.
     */
    public Carta getJuega() {
        return this.juega;
    }

    /**
     * Devuelve la lista de cartas capturadas en esta jugada.
     * 
     * @return La lista de cartas capturadas.
     */
    public List<Carta> getCaptura() {
        return new ArrayList<>(this.captura);
    }

    /**
     * Devuelve la lista de cartas en la mesa resultante de esta jugada.
     * 
     * @return La lista de cartas en la mesa resultante.
     */
    public List<Carta> getMesaResultante() {
        return new ArrayList<>(this.mesaResultante);
    }

}