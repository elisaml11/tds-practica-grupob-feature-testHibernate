package uva.tds.servicios;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uva.tds.entidades.Carta;
import uva.tds.entidades.Jugador;
import uva.tds.ejecutores.AdaptadorCartaJson;

/**
 * Test que reproduce una partida completa de La Escoba
 * siguiendo partida_escoba1.json pero avanzando manualmente
 * sin usar LectorPartida ni cargar JSON.
 */
public class GestorPartidaPartidaManualTest {

    private Jugador ana;
    private Jugador luis;
    private GestorPartida partida;

    @BeforeEach
    void setUp() {
        ana = new Jugador("Ana");
        luis = new Jugador("Luis");
        partida = new GestorPartida(ana, luis);
    }

    /** 
     * Crea una lista de cartas a partir de strings tipo "1-copas", "11-espadas"...
     */
    private ArrayList<Carta> cartas(String... textos) {
        ArrayList<Carta> lista = new ArrayList<>();
        for (String t : textos) {
            lista.add(AdaptadorCartaJson.parse(t));
        }
        return lista;
    }

    @Test
    void testPartidaCompletaSegunJSON() {
        // ----- RONDA 1 -----
        partida.repartoInicial(
            cartas("1-copas","4-copas","11-espadas"),
            cartas("7-oros","2-copas","3-oros"),
            cartas("5-oros","11-oros","10-bastos","11-bastos")
        );

        assertEquals(1, partida.getRondaActual());
        assertEquals(0, partida.getTurnosJugados());
        assertEquals(ana, partida.getJugadorActual());

        // Turnos
        partida.jugarCarta(AdaptadorCartaJson.parse("1-copas"), cartas("5-oros","11-oros"));
        assertEquals(1, partida.getTurnosJugados());
        assertEquals(luis, partida.getJugadorActual());

        partida.jugarCarta(AdaptadorCartaJson.parse("7-oros"), cartas("10-bastos"));
        assertEquals(2, partida.getTurnosJugados());
        assertEquals(ana, partida.getJugadorActual());

        partida.jugarCarta(AdaptadorCartaJson.parse("11-espadas"), cartas());
        assertEquals(3, partida.getTurnosJugados());
        assertEquals(luis, partida.getJugadorActual());

        partida.jugarCarta(AdaptadorCartaJson.parse("2-copas"), cartas());
        assertEquals(4, partida.getTurnosJugados());
        assertEquals(ana, partida.getJugadorActual());

        partida.jugarCarta(AdaptadorCartaJson.parse("4-copas"), cartas("11-espadas","2-copas"));
        assertEquals(5, partida.getTurnosJugados());
        assertEquals(luis, partida.getJugadorActual());

        partida.jugarCarta(AdaptadorCartaJson.parse("3-oros"), cartas());
        assertEquals(6, partida.getTurnosJugados());
        assertTrue(partida.rondaTerminada());
        assertEquals(ana, partida.getJugadorActual()); // Reinicio de turno al finalizar ronda
        assertEquals(cartas("11-bastos","3-oros"), partida.getCartasMesa());

        // ----- RONDA 2 -----
        partida.avanzarRonda();
        assertEquals(2, partida.getRondaActual());
        assertEquals(0, partida.getTurnosJugados());

        partida.repartoRonda(
            cartas("2-espadas","5-espadas","10-espadas"),
            cartas("4-espadas","6-oros","6-bastos")
        );

        partida.jugarCarta(AdaptadorCartaJson.parse("10-espadas"), cartas());
        partida.jugarCarta(AdaptadorCartaJson.parse("4-espadas"), cartas("3-oros","10-espadas"));
        partida.jugarCarta(AdaptadorCartaJson.parse("5-espadas"), cartas());
        partida.jugarCarta(AdaptadorCartaJson.parse("6-oros"), cartas("11-bastos"));
        partida.jugarCarta(AdaptadorCartaJson.parse("2-espadas"), cartas());
        partida.jugarCarta(AdaptadorCartaJson.parse("6-bastos"), cartas());
        assertTrue(partida.rondaTerminada());
        assertEquals(6, partida.getTurnosJugados());
        assertEquals(cartas("5-espadas","2-espadas","6-bastos"), partida.getCartasMesa());

        // ----- RONDA 3 -----
        partida.avanzarRonda();
        assertEquals(0, partida.getTurnosJugados());
        assertEquals(3, partida.getRondaActual());
        partida.repartoRonda(
            cartas("1-espadas","6-espadas","4-bastos"),
            cartas("5-bastos","5-copas","3-bastos")
        );

        partida.jugarCarta(AdaptadorCartaJson.parse("4-bastos"), cartas("5-espadas","6-bastos"));
        partida.jugarCarta(AdaptadorCartaJson.parse("5-bastos"), cartas());
        partida.jugarCarta(AdaptadorCartaJson.parse("6-espadas"), cartas());
        partida.jugarCarta(AdaptadorCartaJson.parse("5-copas"), cartas());
        partida.jugarCarta(AdaptadorCartaJson.parse("1-espadas"), cartas());
        partida.jugarCarta(AdaptadorCartaJson.parse("3-bastos"), cartas("6-espadas","5-copas","1-espadas"));
        assertTrue(partida.rondaTerminada());
        assertEquals(6, partida.getTurnosJugados());
        assertEquals(cartas("2-espadas","5-bastos"), partida.getCartasMesa());

        // ----- RONDA 4 -----
        partida.avanzarRonda();
        assertEquals(4, partida.getRondaActual());
        partida.repartoRonda(
            cartas("3-copas","12-copas","2-bastos"),
            cartas("11-copas","4-oros","1-oros")
        );

        partida.jugarCarta(AdaptadorCartaJson.parse("12-copas"), cartas("5-bastos"));
        partida.jugarCarta(AdaptadorCartaJson.parse("11-copas"), cartas());
        partida.jugarCarta(AdaptadorCartaJson.parse("2-bastos"), cartas());
        partida.jugarCarta(AdaptadorCartaJson.parse("4-oros"), cartas("2-espadas","11-copas"));
        partida.jugarCarta(AdaptadorCartaJson.parse("3-copas"), cartas());
        partida.jugarCarta(AdaptadorCartaJson.parse("1-oros"), cartas());
        assertTrue(partida.rondaTerminada());
        assertEquals(cartas("2-bastos","3-copas","1-oros"), partida.getCartasMesa());

        // ----- RONDA 5 -----
        partida.avanzarRonda();
        assertEquals(5, partida.getRondaActual());
        partida.repartoRonda(
            cartas("6-copas","7-copas","2-oros"),
            cartas("7-espadas","10-oros","1-bastos")
        );

        partida.jugarCarta(AdaptadorCartaJson.parse("2-oros"), cartas());
        partida.jugarCarta(AdaptadorCartaJson.parse("7-espadas"), cartas("2-bastos","3-copas","1-oros","2-oros")); // escoba
        partida.jugarCarta(AdaptadorCartaJson.parse("6-copas"), cartas());
        partida.jugarCarta(AdaptadorCartaJson.parse("10-oros"), cartas());
        partida.jugarCarta(AdaptadorCartaJson.parse("7-copas"), cartas("10-oros"));
        partida.jugarCarta(AdaptadorCartaJson.parse("1-bastos"), cartas());
        assertTrue(partida.rondaTerminada());
        assertEquals(cartas("6-copas","1-bastos"), partida.getCartasMesa());

        // ----- RONDA 6 -----
        partida.avanzarRonda();
        assertEquals(6, partida.getRondaActual());
        partida.repartoRonda(
            cartas("3-espadas","12-espadas","12-oros"),
            cartas("10-copas","12-bastos","7-bastos")
        );

        partida.jugarCarta(AdaptadorCartaJson.parse("12-espadas"), cartas());
        partida.jugarCarta(AdaptadorCartaJson.parse("10-copas"), cartas("6-copas","1-bastos"));
        partida.jugarCarta(AdaptadorCartaJson.parse("12-oros"), cartas());
        partida.jugarCarta(AdaptadorCartaJson.parse("12-bastos"), cartas());
        partida.jugarCarta(AdaptadorCartaJson.parse("3-espadas"), cartas());
        partida.jugarCarta(AdaptadorCartaJson.parse("7-bastos"), cartas());
        assertTrue(partida.rondaTerminada());
        assertEquals(cartas("12-espadas","12-oros","12-bastos","3-espadas","7-bastos"), partida.getCartasMesa());

        // ----- FINAL PARTIDA -----
        partida.finalizarPartida();
        assertEquals(-1, partida.getRondaActual());
        assertThrows(IllegalStateException.class, () -> partida.jugarCarta(AdaptadorCartaJson.parse("1-copas"), cartas()));

        // ---- COMPROBACIONES FINALES ----
        assertEquals(0, ana.getEscobas());
        assertEquals(1, luis.getEscobas());
        assertEquals(13, ana.getNumeroCartasCapturadas());
        assertEquals(27, luis.getNumeroCartasCapturadas());
        assertEquals(3, ana.contarOros());
        assertEquals(7, luis.contarOros());
        assertEquals(1, ana.contarSietes());
        assertEquals(3, luis.contarSietes());
        assertFalse(ana.tieneGuindis());
        assertTrue(luis.tieneGuindis());

        int puntosAna = partida.calcularPuntuacionFinal(ana);
        int puntosLuis = partida.calcularPuntuacionFinal(luis);

        assertEquals(0, puntosAna);
        assertEquals(5, puntosLuis);
    }
}
