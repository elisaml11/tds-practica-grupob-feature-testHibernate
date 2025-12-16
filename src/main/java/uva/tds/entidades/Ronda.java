package uva.tds.entidades;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.CascadeType;
import javax.persistence.Entity;

/**
 * Clase que representa una ronda en una partida de cartas.
 * Cada ronda tiene un número identificador y una lista de turnos asociados.
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
@Entity
@Table(name = "rondas")
public class Ronda {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int numero;

    @ManyToOne(fetch = FetchType.LAZY) // cuando cargas una Ronda, no se carga automática/ la Partida
    @JoinColumn(name = "partida_id")
    private Partida partida;

    @OneToMany(mappedBy = "ronda", cascade = CascadeType.ALL, orphanRemoval = true) // si creas, eliminas o actualizas una Ronda, se hace lo mismo con los Turnos asociados
    private List<Turno> turnos = new ArrayList<>();

    // mesa inicial de la ronda: ManyToMany hacia Carta (tabla ronda_mesa)
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }) // si creas o actualizas una Ronda, se hace lo mismo con las Cartas de la mesa inicial
    @JoinTable(name = "ronda_mesa",
        joinColumns = { @JoinColumn(name = "ronda_id") },
        inverseJoinColumns = { @JoinColumn(name = "carta_id") })
    private List<Carta> mesaInicial = new ArrayList<>();

    public Ronda() { }

    /**
     * Construye una ronda con el número y la lista de turnos especificados.
     * 
     * @param numero El número de la ronda (debe ser >= 1).
     * @param turnos La lista de turnos en esta ronda (si es nula, se considera vacía).
     * @throws IllegalArgumentException Si el número es menor que 1.
     */ 
    public Ronda(int numero, List<Turno> turnos) {
        if (numero < 1) throw new IllegalArgumentException("El número de ronda debe ser >= 1");
        this.numero = numero;
        if (turnos == null) {
            this.turnos = new ArrayList<>();
        } else {
            this.turnos = new ArrayList<>(turnos);
        }
    }

    /**
     * Devuelve el número de la ronda.
     * 
     * @return El número de la ronda.
     */
    public int getNumero() {
        return numero;
    }

    /**
     * Devuelve la lista de turnos en esta ronda.
     * 
     * @return La lista de turnos.
     */
    public List<Turno> getTurnos() {
        return new ArrayList<>(turnos);
    }

}