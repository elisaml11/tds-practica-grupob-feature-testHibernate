package uva.tds.entidades;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests para la clase Jugador
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class JugadorTest {

    private Jugador jugador;

    @BeforeEach
    void setUp() {
        jugador = new Jugador("Marta");
    }

    @Test
    void testValidoCrearJugador(){
        Jugador jugador = new Jugador("Ana");
        assertEquals("Ana", jugador.getNombre());
        assertTrue(jugador.getMano().isEmpty());
        assertTrue(jugador.getCartas().isEmpty());
        assertEquals(0, jugador.getEscobas());
    }

    @Test
    void testValidoConstructorNombreLimiteInterior(){
        Jugador jugador = new Jugador("A");
        assertEquals("A", jugador.getNombre());
    }

    @Test
    void testInvalidoCrearJugadorNombreNulo(){
        assertThrows(IllegalArgumentException.class, () -> {
            new Jugador(null);
        });
    }

    @Test
    void testNoValidoCrearJugadorNombreVacio(){
        assertThrows(IllegalArgumentException.class, () -> {
            new Jugador("");
        });
    }

    @Test
    void testValidoAgregarCartaAMano(){
        Carta carta = new Carta(Palo.OROS, 5);
        jugador.agregarCartaAMano(carta);
        assertEquals(1, jugador.getMano().size());
        assertTrue(jugador.getMano().contains(carta));
    }

    @Test
    void testNoValidoAgregarCartaAManoNula(){
        assertThrows(IllegalArgumentException.class, () -> {
            jugador.agregarCartaAMano(null);
        });
    }

    @Test
    void testValidoAgregarCartaACartas(){
        Carta carta = new Carta(Palo.COPAS, 10);
        jugador.agregarCartaACartas(carta);
        assertEquals(1, jugador.getCartas().size());
        assertTrue(jugador.getCartas().contains(carta));
    }

    @Test
    void testNoValidoAgregarCartaACartasNula(){
        assertThrows(IllegalArgumentException.class, () -> {
            jugador.agregarCartaACartas(null);
        });
    }

    @Test
    void testValidoTirarCarta(){
        Carta carta = new Carta(Palo.ESPADAS, 7);
        jugador.agregarCartaAMano(carta);
        jugador.tirarCarta(carta);
        assertFalse(jugador.getMano().contains(carta));
    }

    @Test
    void testNoValidoTirarCartaNoEnMano(){
        Carta carta = new Carta(Palo.BASTOS, 3);
        assertThrows(IllegalArgumentException.class, () -> {
            jugador.tirarCarta(carta);
        });
    }

    @Test
    void testNoValidoTirarCartaNula(){
        assertThrows(IllegalArgumentException.class, () -> {
            jugador.tirarCarta(null);
        });
    }

    @Test
    void testValidoSumarEscoba(){
        jugador.sumarEscoba();
        jugador.sumarEscoba();
        assertEquals(2, jugador.getEscobas());
    }

    @Test
    void testValidoGetEscobasInicial(){
        assertEquals(0, jugador.getEscobas());
    }

    @Test
    void testContarOrosValidoSinCartas() {
        assertEquals(0, jugador.contarOros());
    }

    @Test
    void testContarOrosValidoConUnOro() {
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 1));
        assertEquals(1, jugador.contarOros());
    }

    @Test
    void testContarOrosValidoConVariosOros() {
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 1));
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 5));
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 7));
        assertEquals(3, jugador.contarOros());
    }

    @Test
    void testContarOrosValidoSinOrosPeroConOtrasCartas() {
        jugador.agregarCartaACartas(new Carta(Palo.COPAS, 1));
        jugador.agregarCartaACartas(new Carta(Palo.ESPADAS, 5));
        jugador.agregarCartaACartas(new Carta(Palo.BASTOS, 7));
        assertEquals(0, jugador.contarOros());
    }

    @Test
    void testContarOrosValidoConOrosMezclados() {
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 1));
        jugador.agregarCartaACartas(new Carta(Palo.COPAS, 2));
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 3));
        jugador.agregarCartaACartas(new Carta(Palo.ESPADAS, 4));
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 5));
        assertEquals(3, jugador.contarOros());
    }

    @Test
    void testContarOrosValidoTodosLosOros() {
        for(int i = 1; i <= 7; i++) {
            jugador.agregarCartaACartas(new Carta(Palo.OROS, i));
        }
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 10));
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 11));
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 12));
        assertEquals(10, jugador.contarOros());
    }

    @Test
    void testContarSietesValidoSinCartas() {
        assertEquals(0, jugador.contarSietes());
    }

    @Test
    void testContarSietesValidoConUnSiete() {
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 7));
        assertEquals(1, jugador.contarSietes());
    }

    @Test
    void testContarSietesValidoConVariosSietes() {
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 7));
        jugador.agregarCartaACartas(new Carta(Palo.COPAS, 7));
        jugador.agregarCartaACartas(new Carta(Palo.ESPADAS, 7));
        assertEquals(3, jugador.contarSietes());
    }

    @Test
    void testContarSietesValidoSinSietesPeroConOtrasCartas() {
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 1));
        jugador.agregarCartaACartas(new Carta(Palo.COPAS, 5));
        jugador.agregarCartaACartas(new Carta(Palo.ESPADAS, 10));
        assertEquals(0, jugador.contarSietes());
    }

    @Test
    void testContarSietesValidoConSietesMezclados() {
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 1));
        jugador.agregarCartaACartas(new Carta(Palo.COPAS, 7));
        jugador.agregarCartaACartas(new Carta(Palo.ESPADAS, 3));
        jugador.agregarCartaACartas(new Carta(Palo.BASTOS, 7));
        assertEquals(2, jugador.contarSietes());
    }

    @Test
    void testContarSietesValidoTodosLosSietes() {
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 7));
        jugador.agregarCartaACartas(new Carta(Palo.COPAS, 7));
        jugador.agregarCartaACartas(new Carta(Palo.ESPADAS, 7));
        jugador.agregarCartaACartas(new Carta(Palo.BASTOS, 7));
        assertEquals(4, jugador.contarSietes());
    }

    @Test
    void testTieneGuindisValidoSinCartas() {
        assertFalse(jugador.tieneGuindis());
    }

    @Test
    void testTieneGuindisValidoConGuindis() {
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 7));
        assertTrue(jugador.tieneGuindis());
    }

    @Test
    void testTieneGuindisValidoSinGuindisPeroConOtrosOros() {
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 1));
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 5));
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 10));
        assertFalse(jugador.tieneGuindis());
    }

    @Test
    void testTieneGuindisValidoSinGuindisPeroConOtrosSietes() {
        jugador.agregarCartaACartas(new Carta(Palo.COPAS, 7));
        jugador.agregarCartaACartas(new Carta(Palo.ESPADAS, 7));
        jugador.agregarCartaACartas(new Carta(Palo.BASTOS, 7));
        assertFalse(jugador.tieneGuindis());
    }

    @Test
    void testTieneGuindisValidoConGuindisYOtrasCartas() {
        jugador.agregarCartaACartas(new Carta(Palo.COPAS, 1));
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 7));
        jugador.agregarCartaACartas(new Carta(Palo.ESPADAS, 5));
        assertTrue(jugador.tieneGuindis());
    }

    @Test
    void testContarCartasCapturadasValidoSinCartas(){
        assertEquals(0, jugador.getNumeroCartasCapturadas());
    }

    @Test
    void testContarCartasCapturadasValidoConVariasCartas(){
        jugador.agregarCartaACartas(new Carta(Palo.OROS, 1));
        jugador.agregarCartaACartas(new Carta(Palo.COPAS, 5));
        jugador.agregarCartaACartas(new Carta(Palo.ESPADAS, 10));
        assertEquals(3, jugador.getNumeroCartasCapturadas());
    }

    
}
