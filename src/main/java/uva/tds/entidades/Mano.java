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
import javax.persistence.Table;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Table(name = "manos")
public class Mano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_ronda", nullable = false)
    private int numeroRonda;

    @Column(name = "nombre_jugador")
    private String nombreJugador;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "mano_cartas",
        joinColumns = { @JoinColumn(name = "mano_id") },
        inverseJoinColumns = { @JoinColumn(name = "carta_id") })
    private List<Carta> cartas = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partida_id")
    private Partida partida;

    public Mano() { }

    public Mano(int numeroRonda, String nombreJugador, List<Carta> cartas) {
        this.numeroRonda = numeroRonda;
        this.nombreJugador = nombreJugador;
        if (cartas != null) this.cartas = new ArrayList<>(cartas);
    }

    public Long getId() { return id; }
    public int getNumeroRonda() { return numeroRonda; }
    public String getNombreJugador() { return nombreJugador; }
    public List<Carta> getCartas() { return new ArrayList<>(cartas); }

    public void setPartida(Partida partida) { this.partida = partida; }
}