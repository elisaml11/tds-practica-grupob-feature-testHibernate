package uva.tds.entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Representación de una partida (puede contener tanto la información detallada
 * de jugadas como un resumen de resultados). 
 *
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
@Entity
@Table(name = "partidas")
public class Partida {

    @Id
    @Column(name = "id", nullable = false, length = 100)
    private String id;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "jugador1")
    private String jugador1;

    @Column(name = "jugador2")
    private String jugador2;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "partida_mesa",
        joinColumns = { @JoinColumn(name = "partida_id") },
        inverseJoinColumns = { @JoinColumn(name = "carta_id") })
    private List<Carta> mesaInicial;

    @OneToMany(mappedBy = "partida", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mano> manosJugadores;

    @OneToMany(mappedBy = "partida", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ronda> rondas;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "resumen_id")
    private ResumenPartida resumen;

    public Partida() {}

    /**
     * Constructor de Partida que inicializa los jugadores, id y fecha.
     * @param id identificador único
     * @param fecha fecha de la partida
     * @param jugador1 nombre jugador 1
     * @param jugador2 nombre jugador 2
     */
    public Partida(String id, LocalDate fecha, String jugador1, String jugador2) {
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
     * Devuelve una lista de nombres de los jugadores.
     * @return lista de nombres de los jugadores
     */
    public ArrayList<String> getNombres() {
        if (this.jugador1 == null || this.jugador2 == null) return new ArrayList<>();
        ArrayList<String> n = new ArrayList<>();
        n.add(this.jugador1);
        n.add(this.jugador2);
        return n;
    }

    /**
     * Devuelve una copia de la mesa inicial.
     * @return mesa inicial
     */
    public ArrayList<Carta> getMesaInicial() {
        return (mesaInicial == null) ? new ArrayList<>() : new ArrayList<>(mesaInicial);
    }

    /**
     * Devuelve una copia de las manos de los jugadores.
     * @return manos de los jugadores
     */
    public ArrayList<ArrayList<Carta>> getManosJugador1() {
        return reconstruirManosParaJugador(this.jugador1);
    }

    /**
     * Devuelve una copia de las manos de los jugadores.
     * @return manos de los jugadores
     */
    public ArrayList<ArrayList<Carta>> getManosJugador2() {
        return reconstruirManosParaJugador(this.jugador2);
    }

    /**
     * Devuelve una copia de las rondas de la partida.
     * @return rondas de la partida
     */
    public List<Ronda> getRondas() {
        return (rondas == null) ? new ArrayList<>() : new ArrayList<>(rondas);
    }

    /**
     * Añade los nombres de los jugadores a la partida.
     * @param nombres lista con los nombres de los jugadores
     */
    public void añadirJugadores(ArrayList<String> nombres){
        if (nombres == null) throw new IllegalArgumentException("El nombre no puede ser nulo");
        this.jugador1 = nombres.get(0);
        this.jugador2 = nombres.get(1);
    }

    /**
     * Añade la mesa inicial a la partida.
     * @param mesa lista con las cartas de la mesa inicial
     */
    public void añadirMesaInicial(ArrayList<Carta> mesa){
        if(mesa == null) throw new IllegalArgumentException("La mesa inicial no puede ser nula");
        this.mesaInicial = new ArrayList<>(mesa);
    }

    /**
     * Añade las manos del jugador 1 a la partida.
     * @param manos lista con las manos del jugador 1
     */
    public void añadirManoJugador1(ArrayList<ArrayList<Carta>> manos){
        if(manos == null) throw new IllegalArgumentException("Las manos del jugador 1 no pueden ser nulas");
        String nombre = (this.jugador1 == null) ? null : this.jugador1;
        convertirYAgregarManosEntidades(manos, nombre);
    }

    /**
     * Añade las manos del jugador 2 a la partida.
     * @param manos lista con las manos del jugador 2
     */
    public void añadirManoJugador2(ArrayList<ArrayList<Carta>> manos){
        if(manos == null) throw new IllegalArgumentException("Las manos del jugador 2 no pueden ser nulas");
        String nombre = (this.jugador2 == null) ? null : this.jugador2;
        convertirYAgregarManosEntidades(manos, nombre);
    }

    /**
     * Añade las rondas a la partida.
     * @param rondas lista con las rondas de la partida
     */
    public void anadirRondas(List<Ronda> rondas) {
        if (rondas == null) throw new IllegalArgumentException("Las rondas no pueden ser nulas");
        this.rondas = new ArrayList<>(rondas);
    }

    /**
     * Establece los resultados finales de la partida (compatibilidad). Internamente
     * crea un ResumenPartida y lo marca como completo.
     * @param j1 Jugador 1 (estado final)
     * @param j2 Jugador 2 (estado final)
     * @param puntos1 puntos para j1
     * @param puntos2 puntos para j2
     */
    public void establecerResultados(Jugador j1, Jugador j2, int puntos1, int puntos2) {
        if (this.id == null) throw new IllegalStateException("La partida no tiene id. Usa el constructor con id y fecha para poder establecer resultados.");
        this.resumen = new ResumenPartida(this.id, this.fecha, j1.getNombre(), j2.getNombre());
        this.resumen.establecerResultados(j1, j2, puntos1, puntos2);
    }

    /**
     * Permite establecer directamente un ResumenPartida calculado.
     * Útil para servicios que calculan el resumen a partir de las jugadas y desean
     * almacenarlo en la entidad Partida aunque esta no tenga id/fecha originales.
     *
     * @param resumen ResumenPartida calculado (no nulo)
     * @throws IllegalArgumentException si resumen es nulo
     */
    public void setResumenPartida(ResumenPartida resumen) {
        if (resumen == null) throw new IllegalArgumentException("El resumen no puede ser nulo");
        this.resumen = resumen;
    }

    /**
     * Indica si la partida (resumen) está completa.
     * @return true si existe un resumen false en caso contrario
     */
    public boolean isCompleta() {
        return this.resumen != null;
    }

    /**     
     * Devuelve el id de la partida.
     * @return id de la partida
     * @throws IllegalStateException si el id no está establecido
     */
    public String getId() {
        if (this.id == null) throw new IllegalStateException("La partida no tiene id");
        return this.id;
    }

    /**     
     * Devuelve la fecha de la partida.
     * @return fecha de la partida
     * @throws IllegalStateException si la fecha no está establecida
     */
    public LocalDate getFecha() {
        if (this.fecha == null) throw new IllegalStateException("La fecha no puede ser nula");
        return this.fecha;
    }

    /**     
     * Devuelve el nombre del jugador 1.
     * @return nombre del jugador 1
     * @throws IllegalStateException si los jugadores no están inicializados
     */
    public String getJugador1() {
        if (this.jugador1 == null) throw new IllegalStateException("Jugadores no inicializados");
        return this.jugador1;
    }

    /**     
     * Devuelve el nombre del jugador 2.
     * @return nombre del jugador 2
     * @throws IllegalStateException si los jugadores no están inicializados
     */
    public String getJugador2() {
        if (this.jugador2 == null) throw new IllegalStateException("Jugadores no inicializados");
        return this.jugador2;
    }

    /**     
     * Verifica que el resumen de la partida esté establecido. 
     * @throws IllegalStateException si el resumen no está establecido
     */
    private void requireResumen() {
        if (this.resumen == null) throw new IllegalStateException("La partida no tiene resultados establecidos");
    }

    public int getPuntosJugador1() { requireResumen(); return this.resumen.getPuntosJugador1(); }
    public int getPuntosJugador2() { requireResumen(); return this.resumen.getPuntosJugador2(); }

    public int getEscobasJugador1() { requireResumen(); return this.resumen.getEscobasJugador1(); }
    public int getEscobasJugador2() { requireResumen(); return this.resumen.getEscobasJugador2(); }

    public int getOrosJugador1() { requireResumen(); return this.resumen.getOrosJugador1(); }
    public int getOrosJugador2() { requireResumen(); return this.resumen.getOrosJugador2(); }

    public int getSietesJugador1() { requireResumen(); return this.resumen.getSietesJugador1(); }
    public int getSietesJugador2() { requireResumen(); return this.resumen.getSietesJugador2(); }

    public boolean isGuindisJugador1() { requireResumen(); return this.resumen.isGuindisJugador1(); }
    public boolean isGuindisJugador2() { requireResumen(); return this.resumen.isGuindisJugador2(); }

    public int getCartasCapturadasJugador1() { requireResumen(); return this.resumen.getCartasCapturadasJugador1(); }
    public int getCartasCapturadasJugador2() { requireResumen(); return this.resumen.getCartasCapturadasJugador2(); }

    public String getGanador() { requireResumen(); return this.resumen.getGanador(); }

    /**
     * Permite obtener el ResumenPartida asociado (si existe).
     * @return ResumenPartida o null si no se han establecido resultados.
     */
    public ResumenPartida getResumenPartida() {
        return this.resumen;
    }

    /**
     * Convierte las manos dadas en entidades Mano y las agrega a la lista de manos de la partida.
     * Cada mano se asocia con el nombre del jugador proporcionado.
     *
     * @param manos Lista de listas de cartas, donde cada lista interna representa una mano en una ronda.
     * @param nombreJugador Nombre del jugador al que pertenecen las manos (puede ser null).
     */
    private void convertirYAgregarManosEntidades(ArrayList<ArrayList<Carta>> manos, String nombreJugador) {
        if (this.manosJugadores == null) {
            this.manosJugadores = new ArrayList<>();
        }

        // Convertir cada mano en una entidad Mano y agregarla a la lista, asociándola con el nombre del jugador
        for (int i = 0; i < manos.size(); i++) {
            int numeroRonda = i + 1;
            List<Carta> cartas = manos.get(i);
            List<Carta> copiaCartas = (cartas == null) ? new ArrayList<>() : new ArrayList<>(cartas); // si cartas es null, crear lista vacía, si no, copia
            Mano mano = new Mano(numeroRonda, nombreJugador, copiaCartas);
            mano.setPartida(this);
            manosJugadores.add(mano);
        }
    }

    /**
     * Reconstruye las manos de un jugador a partir de las entidades Mano almacenadas.
     *
     * @param nombreJugador Nombre del jugador cuyas manos se desean reconstruir 
     *                      (puede ser null para obtener todas las manos).
     * @return Lista de listas de cartas, donde cada lista interna representa una mano del jugador especificado.
     */
    private ArrayList<ArrayList<Carta>> reconstruirManosParaJugador(String nombreJugador) {
        ArrayList<ArrayList<Carta>> resultado = new ArrayList<>();
        if (manosJugadores == null || manosJugadores.isEmpty()) return resultado;

        // Manos que correspondan al nombre
        ArrayList<Mano> seleccionadas = new ArrayList<>();
        for (Mano m : manosJugadores) {
            String nombreExistente = m.getNombreJugador();
            if (nombreJugador == null) {
                seleccionadas.add(m);
            } else {
                if (nombreJugador.equals(nombreExistente)) seleccionadas.add(m);
            }
        }

        // Construir resultado copiando las listas de cartas
        for (Mano m : seleccionadas) {
            List<Carta> cartas = m.getCartas();
            ArrayList<Carta> copia = new ArrayList<>();
            if (cartas != null && !cartas.isEmpty()) {
                copia.addAll(cartas);
            }
            resultado.add(copia);
        }
        return resultado;
    }

}
