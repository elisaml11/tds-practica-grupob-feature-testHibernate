package uva.tds.interfaces;

import java.util.List;


/**
 * Interfaz principal del servicio multijugador para el juego de la Escoba.
 * Define todas las operaciones necesarias para gestionar partidas multijugador
 * 
 * Este servicio está diseñado para ser implementado sobre diferentes
 * tecnologías:
 * - Servidor local (sockets, RMI)
 * - WebSockets / Server-Sent Events
 * - REST API con polling
 * - Mensajería (RabbitMQ, Kafka)
 */
public interface ServicioMultijugador {

    /**
     * Crea una nueva sala de juego.
     * 
     * @param idSala ID único de la sala. No puede ser nulo ni vacío.
     * @param creador   Nombre del jugador creador. No puede ser nulo ni vacío.
     * @param esPrivada Si es true, requiere invitación para unirse.
     * @throws IllegalArgumentException si idSala o creador son nulos o vacíos
     * @throws IllegalStateException    si ya existe una sala con el mismo ID
     */
    void crearSala(String idSala, String creador, boolean esPrivada);

    /**
     * Lista todas las salas disponibles.
     * 
     * @return Lista de identificadores de salas disponibles. Si no hay salas,
     *         devuelve una lista vacía.
     */
    List<String> listarSalasDisponibles();

    /**
     * Une a un jugador a una sala existente.
     * 
     * @param idSala       ID único de la sala. No puede ser nulo ni vacío.
     * @param nombreJugador Nombre del jugador. No puede ser nulo ni vacío.
     * @param codigoAcceso  Código de acceso (null para salas públicas)
     * @throws IllegalArgumentException si idSala o nombreJugador son nulos o
     *                                  vacíos
     * @throws IllegalStateException    si no existe una sala con el ID dado.
     * @throws IllegalStateException    si el jugador ya está en la sala.
     * @throws IllegalStateException    si la sala es privada y el código de
     *                                  acceso es incorrecto.
     * @throws IllegalStateException    si la sala ya tiene el número máximo de
     *                                  jugadores.
     */
    void unirseASala(String idSala, String nombreJugador, String codigoAcceso);

    /**
     * Envía cartas de la mesa.
     * 
     * @param idSala ID único de la sala. No puede ser nulo ni vacío.
     * @param creador   Nombre del jugador creador de la sala. No puede ser nulo
     *                  ni vacío.
     * @param cartas    Lista de cartas a enviar. No puede ser nula ni vacía. Debe seguir el formato numero-palo (1-copas, 2-espadas,...).
     * @throws IllegalArgumentException si idSala, creador o cartas son
     *                                  nulos o vacíos.
     * @throws IllegalArgumentException si el formato de alguna carta es incorrecto.
     * @throws IllegalStateException    si no existe una sala con el ID dado.
     * @throws IllegalStateException    si el jugador no es el creador de la
     *                                  sala.
     */
    void enviarCartasDeMesa(String idSala, String creador, List<String> cartas);

    /**
     * Envía cartas a un jugador.
     * 
     * @param idSala     ID único de la sala. No puede ser nulo ni vacío.
     * @param creador       Nombre del jugador creador de la sala. No puede ser
     *                      nulo
     *                      ni vacío.
     * @param nombreJugador Nombre del jugador. No puede ser nulo ni vacío.
     * @param cartas        Lista de cartas. No puede ser nula ni vacía. Debe seguir el formato numero-palo (1-copas, 2-espadas,...).
     * @throws IllegalArgumentException si idSala, creador, nombreJugador o
     *                                  cartas son nulos o vacíos.
     * @throws IllegalArgumentException si el formato de alguna carta es incorrecto.
     * @throws IllegalStateException    si no existe una sala con el ID dado.
     * @throws IllegalStateException    si creador no es el creador de la
     *                                  sala.
     */
    void enviarCartasDeJugador(String idSala, String creador, String nombreJugador, List<String> cartas);

    /**
     * Obtiene las cartas de la mesa
     * 
     * @param idSala     ID único de la sala. No puede ser nulo ni vacío.
     * @return Lista de cartas de la mesa, o una lista vacía si no hay cartas.
     * @throws IllegalArgumentException si idSala es nulo o vacío
     * @throws IllegalStateException    si no existe una sala con el ID dado.
     */
    List<String> getCartasMesa(String idSala);

   /**
     * Obtiene las cartas de la mano de un jugador.
     * 
     * @param idSala     ID único de la sala. No puede ser nulo ni vacío.
     * @param nombreJugador Nombre del jugador. No puede ser nulo ni vacío.
     * @return Lista de cartas en la mano del jugador, o una lista vacía si no tiene cartas.
     * @throws IllegalArgumentException si idSala o nombreJugador son nulos o
     *                                  vacíos
     * @throws IllegalStateException    si no existe una sala con el ID dado.
     * @throws IllegalStateException    si el jugador no está en la sala.
     */
    List<String> getCartasManoJugador(String idSala,String nombreJugador);

    /**
     * Obtiene la última carta jugada por un jugador.
     * 
     * @param idSala     ID único de la sala. No puede ser nulo ni vacío.
     * @param nombreJugador Nombre del jugador. No puede ser nulo ni vacío.
     * @return La carta jugada por el jugador, o null si no ha jugado ninguna.
     * @throws IllegalArgumentException si idSala o nombreJugador son
     *                                  nulos o vacíos
     * @throws IllegalStateException    si no existe una sala con el ID dado.
     * @throws IllegalStateException    si el jugador no está en la sala.
     */
    String getCartaJugadaPorJugador(String idSala, String nombreJugador);

    /**
     * Obtiene las últimas cartas capturadas por un jugador.
     * 
     * @param idSala     ID único de la sala. No puede ser nulo ni vacío.
     * @param nombreJugador Nombre del jugador. No puede ser nulo ni vacío.
     * @return Lista de cartas capturadas por el jugador, o una lista vacía si no ha
     *         capturado ninguna.
     * @throws IllegalArgumentException si idSala o nombreJugador son nulos o
     *                                  vacíos
     * @throws IllegalStateException    si no existe una sala con el ID dado.
     * @throws IllegalStateException    si el jugador no está en la sala.
     */
    List<String> getCartasCapturadasPorJugador(String idSala, String nombreJugador);

    /**
     * Envia la carta jugada por un jugador.
     * 
     * @param idSala     ID de la sala. No puede ser nulo ni vacío.
     * @param nombreJugador Nombre del jugador que realiza la jugada. No puede ser
     *                      nulo ni vacío.
     * @param carta         Carta a jugar. No puede ser nula. Debe seguir el formato numero-palo (1-copas, 2-espadas,...)
     * @throws IllegalArgumentException si idSala, nombreJugador o carta son
     *                                  nulos o vacíos
     * @throws IllegalStateException    si no existe una sala con el ID dado.
     * @throws IllegalStateException    si el jugador no está en la sala.
     */
    void enviarCartaJugada(String idSala, String nombreJugador, String carta);

    /**
     * Envia las cartas capturadas por un jugador.
     * 
     * @param idSala       ID de la sala. No puede ser nulo ni vacío.
     * @param nombreJugador Nombre del jugador que realiza la jugada. No puede ser
     *                      nulo ni vacío.
     * @param cartasCapturadas Lista de cartas de la mesa a capturar. No puede ser
     *                        nula. Las cartas contenidas deben seguir el formato
     *                        numero-palo (1-copas, 2-espadas,...).
     * @throws IllegalArgumentException si idSala, nombreJugador son
     *                                  nulos o vacíos
     * @throws IllegalArgumentException si cartasCapturadas es nula.
     * @throws IllegalStateException    si no existe una sala con el ID dado.
     * @throws IllegalStateException    si el jugador no está en la sala.
     */
    void enviarCartasCapturadas(String idSala, String nombreJugador, List<String> cartasCapturadas);
}
