package uva.tds.servicios;

import java.util.ArrayList;
import java.util.List;

import uva.tds.entidades.Carta;
import uva.tds.entidades.Jugador;
import uva.tds.interfaces.ServicioMultijugador;
import uva.tds.ejecutores.AdaptadorCartaJson;

/**
 * Servicio de gestión de partidas multijugador para el juego de la Escoba.
 * Utiliza un ServicioMultijugador para la comunicación remota.
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class ServicioJuegoMultijugador {

    private final ServicioMultijugador servicio;
    private String idSalaLocal;

    private String nombreJugadorLocal;
    private GestorPartida gestorPartida;

    private String creadorSalaActual;
    private final ArrayList<String> jugadoresSalaActual = new ArrayList<>();

    /**
     * Constructor del servicio de juego multijugador.
     * 
     * @param servicio ServicioMultijugador para gestionar las partidas
     *                 multijugador.
     * @throws IllegalArgumentException si el servicio es nulo.
     */
    public ServicioJuegoMultijugador(ServicioMultijugador servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio multijugador no puede ser nulo");
        }
        this.servicio = servicio;
    }

    /**
     * Devuelve el servicio multijugador asociado.
     * 
     * @return ServicioMultijugador utilizado para la comunicación remota.
     */
    public ServicioMultijugador getServicio() {
        return servicio;
    }

    /**
     * Devuelve el gestor de la partida actual.
     * 
     * @return GestorPartida que maneja la lógica de la partida local.
     */
    public GestorPartida getGestorPartida() {
        return gestorPartida;
    }

    /**
     * Crea una nueva sala localmente y notifica al servicio remoto.
     * Resetea el estado local asociado a la sala anterior si existiera.
     */
    public void crearPartida(String idSala, String creador, boolean esPrivada) {
        validarStringNoNuloOVacio(idSala);
        validarStringNoNuloOVacio(creador);

        servicio.crearSala(idSala, creador, esPrivada);

        this.idSalaLocal = idSala;
        this.nombreJugadorLocal = creador;
        this.creadorSalaActual = creador;
        this.jugadoresSalaActual.clear();
        this.jugadoresSalaActual.add(creador);
        this.gestorPartida = null;
    }

    /**
     * Une al jugador local a una sala existente y notifica al servicio remoto.
     * Resetea el estado local asociado a la sala anterior si existiera.
     * 
     * @param idSala        ID de la sala a unirse.
     * @param nombreJugador Nombre del jugador local.
     * @param codigoAcceso  Código de acceso para salas privadas (puede ser null).
     * @throws IllegalArgumentException si idSala o nombreJugador son nulos o
     *                                  vacíos.
     */
    public void unirseASala(String idSala, String nombreJugador, String codigoAcceso) {
        validarStringNoNuloOVacio(idSala);
        validarStringNoNuloOVacio(nombreJugador);
        if (this.idSalaLocal == null || !this.idSalaLocal.equals(idSala)) {
            this.idSalaLocal = idSala;
            this.creadorSalaActual = null;
            this.jugadoresSalaActual.clear();
            this.gestorPartida = null;
            this.nombreJugadorLocal = nombreJugador;
        }
        servicio.unirseASala(idSala, nombreJugador, codigoAcceso);
        if (!jugadoresSalaActual.contains(nombreJugador)) {
            jugadoresSalaActual.add(nombreJugador);
        }
        if (this.creadorSalaActual == null && !jugadoresSalaActual.isEmpty()) {
            this.creadorSalaActual = jugadoresSalaActual.get(0);
        }
    }

    /**
     * Lista las salas disponibles consultando al servicio remoto.
     * 
     * @return Lista de IDs de salas disponibles.
     */
    public List<String> listarSalasDisponibles() {
        return servicio.listarSalasDisponibles();
    }

    /**
     * Inicializa la partida localmente (crea GestorPartida) a partir de los nombres
     * de los jugadores
     * registrados localmente en esta sala.
     * 
     * @throws IllegalStateException si no hay exactamente 2 jugadores en la sala
     *                               actual.
     */
    public void inicializarPartida() {
        if (jugadoresSalaActual.size() != 2) {
            throw new IllegalStateException("Se necesitan exactamente 2 jugadores para iniciar la partida");
        }
        Jugador j1 = new Jugador(jugadoresSalaActual.get(0));
        Jugador j2 = new Jugador(jugadoresSalaActual.get(1));
        this.gestorPartida = new GestorPartida(j1, j2);
    }

    /**
     * Reparto inicial que solo el creador puede invocar.
     * 
     * @param cartasMesa     Cartas para la mesa (4 cartas).
     * @param cartasJugador1 Cartas para el jugador 1 (3 cartas).
     * @param cartasJugador2 Cartas para el jugador 2 (3 cartas).
     * @throws IllegalArgumentException si alguna lista de cartas es nula o tiene un
     *                                  número incorrecto de cartas.
     * @throws IllegalStateException    si el jugador local no es el creador de la
     *                                  sala o si la partida no ha sido
     *                                  inicializada.
     */
    public void repartirCartasInicial(List<String> cartasMesa, List<String> cartasJugador1,
            List<String> cartasJugador2) {
        validarEsCreador();
        validarPartidaInicializada();

        if (cartasMesa == null || cartasJugador1 == null || cartasJugador2 == null) {
            throw new IllegalArgumentException("Las listas de cartas no pueden ser nulas");
        }
        if (cartasMesa.size() != 4 || cartasJugador1.size() != 3 || cartasJugador2.size() != 3) {
            throw new IllegalArgumentException("Número incorrecto de cartas para el reparto inicial");
        }

        servicio.enviarCartasDeMesa(idSalaLocal, creadorSalaActual, cartasMesa);
        servicio.enviarCartasDeJugador(idSalaLocal, creadorSalaActual, jugadoresSalaActual.get(0), cartasJugador1);
        servicio.enviarCartasDeJugador(idSalaLocal, creadorSalaActual, jugadoresSalaActual.get(1), cartasJugador2);

        ArrayList<Carta> mesa = convertirACartas(cartasMesa);
        ArrayList<Carta> mano1 = convertirACartas(cartasJugador1);
        ArrayList<Carta> mano2 = convertirACartas(cartasJugador2);

        gestorPartida.repartoInicial(mano1, mano2, mesa);
    }

    /**
     * Reparto de ronda posterior a la inicial.
     * Se requiere que quien invoque sea el creador.
     * 
     * @param cartasJugador1 Cartas para el jugador 1 (3 cartas).
     * @param cartasJugador2 Cartas para el jugador 2 (3 cartas).
     * @throws IllegalArgumentException si alguna lista de cartas es nula o tiene un
     *                                  número incorrecto de cartas.
     * @throws IllegalStateException    si el jugador local no es el creador de la
     *                                  sala o si la partida no ha sido
     *                                  inicializada.
     */
    public void repartirCartasRonda(List<String> cartasJugador1, List<String> cartasJugador2) {
        validarEsCreador();
        validarPartidaInicializada();

        if (cartasJugador1 == null || cartasJugador2 == null) {
            throw new IllegalArgumentException("Las listas de cartas no pueden ser nulas");
        }
        if (cartasJugador1.size() != 3 || cartasJugador2.size() != 3) {
            throw new IllegalArgumentException("Cada jugador debe recibir exactamente 3 cartas");
        }

        servicio.enviarCartasDeJugador(idSalaLocal, creadorSalaActual, jugadoresSalaActual.get(0), cartasJugador1);
        servicio.enviarCartasDeJugador(idSalaLocal, creadorSalaActual, jugadoresSalaActual.get(1), cartasJugador2);

        ArrayList<Carta> mano1 = convertirACartas(cartasJugador1);
        ArrayList<Carta> mano2 = convertirACartas(cartasJugador2);

        gestorPartida.repartoRonda(mano1, mano2);
    }

    /**
     * Realiza una jugada localmente y notifica al servidor.
     * 
     * @param cartaJugada     Carta jugada en formato numero-palo (1-copas,
     *                        2-espadas,...).
     * @param cartasACapturar Cartas a capturar en formato numero-palo (1-copas,
     *                        2-espadas,...).
     * @throws IllegalArgumentException si cartaJugada es nula o vacía, o si
     *                                  cartasACapturar es nula.
     * @throws IllegalStateException    si la partida no ha sido inicializada o si
     *                                  el nombre del jugador
     *                                  local no está establecido.
     */
    public void realizarJugada(String cartaJugada, List<String> cartasACapturar) {
        validarPartidaInicializada();
        validarStringNoNuloOVacio(cartaJugada);
        validarStringNoNuloOVacio(nombreJugadorLocal);
        if (cartasACapturar == null) {
            throw new IllegalArgumentException("Las cartas a capturar no pueden ser nulas");
        }

        Carta carta = AdaptadorCartaJson.parse(cartaJugada);
        ArrayList<Carta> capturas = convertirACartas(cartasACapturar);

        // aplicar localmente (optimistic update)
        gestorPartida.jugarCarta(carta, capturas);

        // notificar al servidor (si lanza excepción, se propagará)
        servicio.enviarCartaJugada(idSalaLocal, nombreJugadorLocal, cartaJugada);
        if (!cartasACapturar.isEmpty()) {
            servicio.enviarCartasCapturadas(idSalaLocal, nombreJugadorLocal, cartasACapturar);
        }
    }

    /**
     * Recibe la jugada del rival desde el servidor y la aplica localmente.
     * 
     * @param nombreJugadorRival Nombre del jugador rival.
     * @throws IllegalArgumentException si el nombre del jugador rival es nulo o
     *                                  vacío.
     * @throws IllegalStateException    si la partida no ha sido inicializada o si
     *                                  no hay jugada disponible del rival.
     */
    public void recibirJugadaRival(String nombreJugadorRival) {
        validarPartidaInicializada();
        validarStringNoNuloOVacio(nombreJugadorRival);

        // verificamos que el jugador participa en la partida local
        Jugador rival;
        if (gestorPartida.getJugador1().getNombre().equals(nombreJugadorRival)) {
            rival = gestorPartida.getJugador1();
        } else if (gestorPartida.getJugador2().getNombre().equals(nombreJugadorRival)) {
            rival = gestorPartida.getJugador2();
        } else {
            throw new IllegalArgumentException("El jugador " + nombreJugadorRival + " no participa en la partida local");
        }
        String cartaJugadaStr = servicio.getCartaJugadaPorJugador(idSalaLocal, nombreJugadorRival);
        List<String> cartasCapturadasStr = servicio.getCartasCapturadasPorJugador(idSalaLocal, nombreJugadorRival);

        if (cartaJugadaStr == null) {
            throw new IllegalStateException("No hay jugada disponible del rival");
        }

        Carta cartaJugada = AdaptadorCartaJson.parse(cartaJugadaStr);

        ArrayList<Carta> cartasCapturadas;
        if (cartasCapturadasStr == null) {
            cartasCapturadas = new ArrayList<>();
        } else {
            cartasCapturadas = convertirACartas(cartasCapturadasStr);
        }

        // Validar la jugada recibida contra el estado local antes de aplicarla
        validarJugadaRemota(rival, cartaJugada, cartasCapturadas);

        // establecer jugador actual en el gestor antes de aplicar la jugada remota
        gestorPartida.establecerJugadorActual(rival);
        gestorPartida.jugarCarta(cartaJugada, cartasCapturadas);
    }

    /**
     * Avanza la ronda actual.
     * 
     * @throws IllegalStateException si la partida no ha sido inicializada.
     */
    public void avanzarRonda() {
        validarPartidaInicializada();
        gestorPartida.avanzarRonda();
    }

    /**
     * Finaliza la partida actual.
     * 
     * @throws IllegalStateException si la partida no ha sido inicializada.
     */
    public void finalizarPartida() {
        validarPartidaInicializada();
        gestorPartida.finalizarPartida();
    }

    /**
     * Obtiene las cartas actualmente en la mesa.
     * 
     * @return ArrayList de cartas en la mesa.
     * @throws IllegalStateException si la partida no ha sido inicializada.
     */
    public ArrayList<Carta> obtenerCartasMesa() {
        validarPartidaInicializada();
        return gestorPartida.getCartasMesa();
    }

    /**
     * Obtiene el jugador actual.
     * 
     * @return Jugador que tiene el turno actual.
     * @throws IllegalStateException si la partida no ha sido inicializada.
     */
    public Jugador obtenerJugadorActual() {
        validarPartidaInicializada();
        return gestorPartida.getJugadorActual();
    }

    /**
     * Valida que el jugador local es el creador de la sala.
     * 
     * @throws IllegalStateException si el jugador local no es el creador de la sala
     *                               o si el nombre del jugador local no está
     *                               establecido.
     */
    private void validarEsCreador() {
        if (creadorSalaActual == null || !creadorSalaActual.equals(nombreJugadorLocal)) {
            throw new IllegalStateException("Solo el creador de la sala puede repartir cartas");
        }
    }

    private void validarStringNoNuloOVacio(String valor) {
        if (valor == null || valor.isEmpty()) {
            throw new IllegalArgumentException("El valor no puede ser nulo o vacío");
        }
    }

    /**
     * Valida que la partida ha sido inicializada.
     * 
     * @throws IllegalStateException si la partida no ha sido inicializada.
     */
    private void validarPartidaInicializada() {
        if (gestorPartida == null) {
            throw new IllegalStateException("La partida no ha sido inicializada");
        }
    }

    /**
     * Convierte una lista de cadenas JSON a una lista de objetos Carta.
     * 
     * @param cartasStr Lista de cadenas JSON que representan cartas.
     * @return ArrayList de objetos Carta.
     */
    private ArrayList<Carta> convertirACartas(List<String> cartasStr) {
        ArrayList<Carta> cartas = new ArrayList<>();
        for (String cartaStr : cartasStr) {
            cartas.add(AdaptadorCartaJson.parse(cartaStr));
        }
        return cartas;
    }

    /**
     * Valida la jugada recibida del rival contra el estado local.
     * 
     * @param rival            Jugador rival que realiza la jugada.
     * @param cartaJugada      Carta jugada por el rival.
     * @param cartasCapturadas Cartas que el rival intenta capturar.
     * @throws IllegalStateException si la jugada no es válida según las reglas del
     *                               juego.
     */
    private void validarJugadaRemota(Jugador rival, Carta cartaJugada, ArrayList<Carta> cartasCapturadas) {
        // Comprobar que la carta jugada está en la mano del rival
        if (!rival.getMano().contains(cartaJugada)) {
            throw new IllegalStateException("La carta jugada no está en la mano del rival: " + cartaJugada);
        }

        // Comprobar que las cartas a capturar están en la mesa
        ArrayList<Carta> mesa = gestorPartida.getCartasMesa();
        for (Carta c : cartasCapturadas) {
            if (!mesa.contains(c)) {
                throw new IllegalStateException("La carta a capturar no está en la mesa: " + c);
            }
        }

        // Si hay captura, la suma de valores debe ser 15 (incluyendo la carta jugada)
        if (!cartasCapturadas.isEmpty()) {
            int suma = valorCarta(cartaJugada);
            for (Carta c : cartasCapturadas)
                suma += valorCarta(c);
            if (suma != 15) {
                throw new IllegalStateException("Las cartas no suman 15 (suman " + suma + ")");
            }
        }
    }

    /**
     * Obtiene el valor de una carta según las reglas del juego de la Escoba.
     * 
     * @param c Carta cuyo valor se desea obtener.
     * @return Valor entero de la carta.
     */
    private int valorCarta(Carta c) {
        int idx = c.getIndice();
        if (idx == 12)
            return 10;
        if (idx == 11)
            return 9;
        if (idx == 10)
            return 8;
        return idx;
    }
}