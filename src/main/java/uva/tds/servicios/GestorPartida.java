package uva.tds.servicios;

import java.util.ArrayList;

import uva.tds.entidades.Baraja;
import uva.tds.entidades.Carta;
import uva.tds.entidades.Jugador;

/**
 * Clase que gestiona una partida de La Escoba entre dos jugadores.
 * Permite el manejo de rondas, turnos, reparto de cartas,
 * jugadas y cálculo de puntuaciones.
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class GestorPartida {
    private final Jugador jugador1;
    private final Jugador jugador2;
    private final Baraja baraja;
    private final GestorRonda gestorRonda;
    private final GestorTurno gestorTurno;
    private int rondaActual;
    private Jugador ultimoQueHizoBaza;
    private static final ArrayList<Integer> SUMAS_VALIDAS_FINAL = new ArrayList<>(java.util.List.of(10, 25, 40, 55));
    private static final int TURNOS_POR_RONDA = 6;

    /**
     * Constructor de GestorPartida.
     * 
     * @param jugador1 Primer jugador de la partida.
     * @param jugador2 Segundo jugador de la partida.
     * @throws IllegalArgumentException si alguno de los jugadores es nulo.
     */
    public GestorPartida(Jugador jugador1, Jugador jugador2) {
        if (jugador1 == null || jugador2 == null) {
            throw new IllegalArgumentException("Los jugadores no pueden ser nulos");
        }
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.baraja = new Baraja();
        this.rondaActual = 1;
        this.gestorRonda = new GestorRonda(this.rondaActual);
        ArrayList<Jugador> jugadores = new ArrayList<>();
        jugadores.add(jugador1);
        jugadores.add(jugador2);
        this.gestorTurno = new GestorTurno(jugadores, TURNOS_POR_RONDA);
        this.gestorTurno.iniciarCon(jugador1);
        this.ultimoQueHizoBaza = null;
    }

    /**
     * Obtiene el primer jugador de la partida.
     * 
     * @return El primer jugador.
     */
    public Jugador getJugador1() {
        return this.jugador1;
    }

    /**
     * Obtiene el segundo jugador de la partida.
     * 
     * @return El segundo jugador.
     */
    public Jugador getJugador2() {
        return this.jugador2;
    }

    /**
     * Obtiene el jugador actual en turno.
     * 
     * @return El jugador actual.
     */
    public Jugador getJugadorActual() {
        return this.gestorTurno.getJugadorActual();
    }

    /**
     * Obtiene la baraja utilizada en la partida.
     * 
     * @return La baraja.
     */
    public Baraja getBaraja() {
        return this.baraja;
    }

    /**
     * Obtiene la ronda actual de la partida.
     * 
     * @return La ronda actual.
     */
    public int getRondaActual() {
        return this.rondaActual;
    }

    /**
     * Obtiene el número de turnos jugados en la ronda actual.
     * 
     * @return El número de turnos jugados.
     */
    public int getTurnosJugados() {
        return this.gestorTurno.getTurnosJugados();
    }

    /**
     * Obtiene las cartas actualmente en la mesa.
     * 
     * @return Las cartas en la mesa.
     */
    public ArrayList<Carta> getCartasMesa() {
        return this.gestorRonda.getCartasMesa();
    }

    /**
     * Obtiene el último jugador que hizo una baza.
     * 
     * @return El último jugador que hizo una baza.
     */
    public Jugador getUltimoQueHizoBaza() {
        return this.ultimoQueHizoBaza;
    }

    /**
     * Indica si la ronda actual ha terminado.
     * 
     * @return true si la ronda ha terminado, false en caso contrario.
     */
    public boolean rondaTerminada() {
        return this.gestorTurno.rondaTerminada();
    }

    /**
     * Realiza el reparto inicial de cartas a los jugadores y la mesa.
     * 
     * @param cartasJugador1      Las cartas del jugador 1.
     * @param cartasJugador2      Las cartas del jugador 2.
     * @param cartasMesaIniciales Las cartas iniciales en la mesa.
     * @throws IllegalStateException    Si no es la ronda 1.
     * @throws IllegalArgumentException Si alguna de las listas es nula.
     * @throws IllegalArgumentException Si el numero de cartas en las listas no es
     *                                  correcto, tiene que ser 334.
     */
    public void repartoInicial(ArrayList<Carta> cartasJugador1, ArrayList<Carta> cartasJugador2,
            ArrayList<Carta> cartasMesaIniciales) {
        if (this.rondaActual != 1)
            throw new IllegalStateException("El reparto inicial solo se puede hacer en la ronda 1");
        if (cartasJugador1 == null || cartasJugador2 == null || cartasMesaIniciales == null)
            throw new IllegalArgumentException("Listas de reparto no pueden ser nulas");
        if (cartasJugador1.size() != 3 || cartasJugador2.size() != 3 || cartasMesaIniciales.size() != 4)
            throw new IllegalArgumentException("Número incorrecto de cartas para el reparto inicial");

        repartirAMano(this.jugador1, cartasJugador1);
        repartirAMano(this.jugador2, cartasJugador2);
        // robar cartas de la baraja para la mesa e insertarlas en el gestorRonda
        this.gestorRonda.setCartasMesa(cartasMesaIniciales);
        for (Carta c : cartasMesaIniciales)
            this.baraja.robarCarta(c);
    }

    /**
     * Realiza el reparto de cartas a los jugadores en rondas posteriores a la
     * primera.
     * 
     * @param cartasJugador1 Las cartas del jugador 1.
     * @param cartasJugador2 Las cartas del jugador 2.
     * @throws IllegalStateException    Si la partida ha finalizado.
     * @throws IllegalStateException    Si es la primera ronda.
     * @throws IllegalArgumentException Si alguna de las listas es nula.
     * @throws IllegalArgumentException Si el número de cartas a repartir no es 3.
     */
    public void repartoRonda(ArrayList<Carta> cartasJugador1, ArrayList<Carta> cartasJugador2) {
        if (this.rondaActual == -1)
            throw new IllegalStateException("No se puede repartir si la partida ha finalizado.");
        if (this.rondaActual == 1)
            throw new IllegalStateException("No se puede repartir durante la primera ronda.");
        if (cartasJugador1 == null || cartasJugador2 == null)
            throw new IllegalArgumentException("Las listas de cartas no pueden ser nulas.");
        if (cartasJugador1.size() != 3 || cartasJugador2.size() != 3)
            throw new IllegalArgumentException("Cada jugador debe recibir exactamente 3 cartas.");

        repartirAMano(this.jugador1, cartasJugador1);
        repartirAMano(this.jugador2, cartasJugador2);

        // al empezar nueva ronda, el jugador actual es jugador1
        this.gestorTurno.establecerJugadorActual(this.jugador1);
    }


    /**
     * Juega una carta y, si es necesario, captura cartas de la mesa.
     * 
     * @param cartaJugada     La carta que se está jugando.
     * @param cartasACapturar Las cartas que se desean capturar, puede estar vacía.
     * @throws IllegalStateException    si la partida o la ronda han finalizado.
     * @throws IllegalArgumentException si los parámetros son inválidos
     * @throws IllegalArgumentException si la carta jugada no está en la mano del
     *                                  jugador actual.
     */
    public void jugarCarta(Carta cartaJugada, ArrayList<Carta> cartasACapturar) {
        validarEstadoYParametrosJugada(cartaJugada, cartasACapturar);
        Jugador actual = this.getJugadorActual();
        if (!actual.getMano().contains(cartaJugada))
            throw new IllegalArgumentException("El jugador no tiene esa carta en su mano");

        if (!cartasACapturar.isEmpty()) {
            capturarCartas(cartaJugada, cartasACapturar);
        } else {
            actual.tirarCarta(cartaJugada);
            this.gestorRonda.annadeCartaMesa(cartaJugada);
        }

        this.gestorTurno.cambiarTurno();
        if (this.gestorTurno.rondaTerminada())
            this.gestorTurno.establecerJugadorActual(this.jugador1);
    }

    /**
     * Captura las cartas de la mesa si la suma con la carta jugada es 15.
     * 
     * @param cartaJugada     La carta que se está jugando.
     * @param cartasACapturar Las cartas que se desean capturar.
     * @throws IllegalStateException    si la partida o la ronda han finalizado.
     * @throws IllegalArgumentException si los parámetros son inválidos
     * @throws IllegalArgumentException si la carta jugada no está en la mano del
     *                                  jugador actual.
     * @throws IllegalArgumentException si alguna de las cartas a capturar no está
     *                                  en la mesa.
     */
    public void capturarCartas(Carta cartaJugada, ArrayList<Carta> cartasACapturar) {
        validarEstadoYParametrosJugada(cartaJugada, cartasACapturar);
        Jugador actual = this.getJugadorActual();
        if (!actual.getMano().contains(cartaJugada))
            throw new IllegalArgumentException("El jugador no tiene esa carta en su mano");

        // comprobar que las cartas están en la mesa
        for (Carta c : cartasACapturar) {
            if (!this.gestorRonda.getCartasMesa().contains(c)) {
                throw new IllegalArgumentException("La carta " + c + " no está en la mesa");
            }
        }
        int suma = calcularValorCarta(cartaJugada);
        for (Carta c : cartasACapturar)
            suma += calcularValorCarta(c);
        if (suma != 15)
            throw new IllegalArgumentException("Las cartas no suman 15 (suman " + suma + ")");

        boolean esEscoba = (capturaTodasLasCartasDeLaMesa(cartasACapturar));

        actual.tirarCarta(cartaJugada);
        actual.agregarCartaACartas(cartaJugada);

        this.gestorRonda.eliminaCartasMesa(cartasACapturar);
        for (Carta c : cartasACapturar) {
            actual.agregarCartaACartas(c);
        }
        this.ultimoQueHizoBaza = actual;
        if (esEscoba) {
            actual.sumarEscoba();
        }
    }


    /**
     * Avanza a la siguiente ronda de la partida.
     * Reinicia los turnos y establece al primer jugador como jugador actual.
     * Finaliza la partida si no hay cartas en la baraja y se ha completado la
     * ronda.
     */
    public void avanzarRonda() {
        if (this.rondaActual == -1)
            throw new IllegalStateException("La partida ya ha finalizado");
        if (this.baraja.getNumeroDeCartas() == 0 && this.rondaActual >= TURNOS_POR_RONDA) {
            finalizarPartida();
            return;
        }
        this.rondaActual += 1;
        this.gestorRonda.incrementarNumero();
        // al avanzar de ronda se reinician los turnos y el jugador actual es jugador1
        this.gestorTurno.reiniciarTurnos();
        this.gestorTurno.establecerJugadorActual(this.jugador1);
    }

    /**
     * Finaliza la partida, asignando las cartas restantes en la mesa
     * al último jugador que hizo una baza o al jugador actual si nadie hizo baza.
     * Verifica que las manos de los jugadores estén vacías y que la suma
     * de las cartas en la mesa sea válida.
     * 
     * @throws IllegalStateException si la partida ya ha finalizado,
     *                               o si las manos de los jugadores no están
     *                               vacías,
     *                               o si la suma de las cartas en la mesa no es
     *                               válida.
     */
    public void finalizarPartida() {
        if (this.rondaActual == -1)
            throw new IllegalStateException("La partida ya ha finalizado");

        if (!jugador1.getMano().isEmpty() || !jugador2.getMano().isEmpty()) {
            throw new IllegalStateException("Al finalizar la partida, las manos de los jugadores deben estar vacías");
        }

        // suma de mesa válida
        ArrayList<Carta> mesa = this.gestorRonda.getCartasMesa();
        if (!mesa.isEmpty()) {
            int suma = this.gestorRonda.sumaCartas();
            if (!SUMAS_VALIDAS_FINAL.contains(suma)) {
                throw new IllegalStateException("Suma final de cartas en mesa inválida: " + suma);
            }
            // asignar cartas al receptor
            Jugador receptor = (this.ultimoQueHizoBaza != null) ? this.ultimoQueHizoBaza : getJugadorActual();
            this.gestorRonda.asignarCartasAJugador(receptor);
        }

        this.rondaActual = -1;
    }
    

    /**
     * Calcula la puntuación final de un jugador según las reglas del juego.
     * 
     * @param jugador El jugador cuya puntuación se va a calcular.
     * @return La puntuación final del jugador.
     */
    public int calcularPuntuacionFinal(Jugador jugador) {
        int puntos = 0;
        Jugador otro = (jugador == this.jugador1) ? this.jugador2 : this.jugador1;
        puntos += jugador.getEscobas();
        if (jugador.contarSietes() == 4) {
            puntos += 3;
        } else {
            if (jugador.tieneGuindis())
                puntos += 1;
        }
        if (jugador.contarSietes() > otro.contarSietes())
            puntos += 1;
        if (jugador.contarOros() == 10)
            puntos += 2;
        else if (jugador.contarOros() > otro.contarOros())
            puntos += 1;
        if (jugador.getNumeroCartasCapturadas() > otro.getNumeroCartasCapturadas())
            puntos += 1;
        return puntos;
    }

    /**
     * Cambia el turno al siguiente jugador.
     * 
     * @throws IllegalStateException si la partida ya ha finalizado.
     */
    public void cambiarTurno() {
        if (this.rondaActual == -1) {
            throw new IllegalStateException("La partida ya ha finalizado");
        }
        this.gestorTurno.cambiarTurno();
    }

    /**
     * Reinicia los turnos para una nueva ronda.
     * Establece al primer jugador como jugador actual.
     * 
     * @throws IllegalStateException si la partida ya ha finalizado
     *                               o si la ronda no ha terminado aún.
     */
    public void reiniciarTurnos() {
        if (this.rondaActual == -1) {
            throw new IllegalStateException("La partida ya ha finalizado");
        }
        if (!rondaTerminada()) {
            throw new IllegalStateException("La ronda no ha terminado aún");
        }
        this.gestorTurno.reiniciarTurnos();
        establecerJugadorActual(this.jugador1);
    }

    /**
     * Reparte las cartas dadas a la mano del jugador especificado.
     * 
     * @param jugador El jugador al que se le repartirán las cartas.
     * @param cartas  Las cartas a repartir.
     */
    private void repartirAMano(Jugador jugador, ArrayList<Carta> cartas) {
        for (Carta c : cartas) {
            this.baraja.robarCarta(c);
            jugador.agregarCartaAMano(c);
        }
    }

    /**
     * Verifica si se han capturado todas las cartas de la mesa.
     * 
     * @param cartasACapturar Las cartas que se desean capturar.
     * @return true si se capturan todas las cartas de la mesa, false en caso
     *         contrario.
     */
    private boolean capturaTodasLasCartasDeLaMesa(ArrayList<Carta> cartasACapturar) {
        ArrayList<Carta> mesa = this.gestorRonda.getCartasMesa();
        return cartasACapturar.size() == mesa.size();
    }

    /**
     * Calcula el valor de una carta según las reglas del juego.
     * 
     * @param carta La carta cuyo valor se va a calcular.
     * @return El valor de la carta.
     */
    private int calcularValorCarta(Carta carta) {
        int indice = carta.getIndice();
        if (indice == 12)
            return 10;
        if (indice == 11)
            return 9;
        if (indice == 10)
            return 8;
        return indice;
    }

    /**
     * Valida que la partida y el turno estén en un estado correcto
     * y que los parámetros de jugada sean válidos.
     * 
     * @param cartaJugada     carta que se va a jugar.
     * @param cartasACapturar cartas que se van a capturar (puede estar vacía).
     * @throws IllegalStateException    si la partida o la ronda han finalizado.
     * @throws IllegalArgumentException si la carta o la lista de cartas son nulas.
     */
    private void validarEstadoYParametrosJugada(Carta cartaJugada, ArrayList<Carta> cartasACapturar) {
        if (this.rondaActual == -1)
            throw new IllegalStateException("La partida ya ha finalizado");
        if (this.gestorTurno.rondaTerminada())
            throw new IllegalStateException("La ronda ya ha terminado");
        if (cartaJugada == null)
            throw new IllegalArgumentException("La carta no puede ser nula");
        if (cartasACapturar == null)
            throw new IllegalArgumentException("Las cartas a capturar no pueden ser nulas");
    }

    public void establecerJugadorActual(Jugador jugador) {
        this.gestorTurno.establecerJugadorActual(jugador);
    }

}