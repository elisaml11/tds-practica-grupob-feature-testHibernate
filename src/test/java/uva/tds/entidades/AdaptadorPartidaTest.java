package uva.tds.entidades;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import uva.tds.servicios.GestorPartida;

/**
 * Tests para la clase AdaptadorPartida
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class AdaptadorPartidaTest {

    @Test
    void testConvertirValidoIdLimiteInferior() {
        Jugador a = new Jugador("a");
        Jugador b = new Jugador("b");

        a.agregarCartaACartas(new Carta(Palo.OROS, 7)); 
        a.sumarEscoba(); 

        GestorPartida gestor = new GestorPartida(a, b);
        AdaptadorPartida adaptador = new AdaptadorPartida();

        LocalDate fecha = LocalDate.of(2025, 11, 8);
        ResumenPartida partida = adaptador.convertir("x", fecha, gestor);

        assertEquals("x", partida.getId());
        assertEquals(fecha, partida.getFecha());
        assertEquals("a", partida.getJugador1());
        assertEquals(a.getEscobas(), partida.getEscobasJugador1());
        assertTrue(partida.isCompleta());
    }

    @Test
    void testConvertirNoValidoGestorNull(){
        AdaptadorPartida adaptador = new AdaptadorPartida();
        LocalDate fecha = LocalDate.of(2025, 11, 8);
        assertThrows(IllegalArgumentException.class, () -> adaptador.convertir("id1", fecha, null));
    }

    @Test
    void testConvertirNoValidoIdVacio(){
        AdaptadorPartida adaptador = new AdaptadorPartida();
        LocalDate fecha = LocalDate.of(2025, 11, 8);
        GestorPartida gestor = new GestorPartida(new Jugador("A"), new Jugador("B"));
        assertThrows(IllegalArgumentException.class, () -> adaptador.convertir("", fecha, gestor));
    }

    @Test
    void testConvertirNoValidoFechaNull(){
        AdaptadorPartida adaptador = new AdaptadorPartida();
        GestorPartida gestor = new GestorPartida(new Jugador("A"), new Jugador("B"));
        assertThrows(IllegalArgumentException.class, () -> adaptador.convertir("id1", null, gestor));
    }

    @Test
    void testConvertirNoValidoIdNulo(){
        AdaptadorPartida adaptador = new AdaptadorPartida();
        LocalDate fecha = LocalDate.of(2025, 11, 8);
        GestorPartida gestor = new GestorPartida(new Jugador("A"), new Jugador("B"));
        assertThrows(IllegalArgumentException.class, () -> adaptador.convertir(null, fecha, gestor));
    }
}