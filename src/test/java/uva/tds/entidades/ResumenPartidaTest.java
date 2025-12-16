package uva.tds.entidades;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

/**
 * Tests para la clase Partida
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class ResumenPartidaTest {


    private LocalDate fecha;

    @BeforeEach
    void setUp() {
        fecha = LocalDate.of(2025, 11, 8);
    }

    
    @Test
    void testConstructorValidoIdentificadorLimiteInferior() {
        ResumenPartida partida = new ResumenPartida("a", fecha, "b", "c");
        
        assertEquals("a", partida.getId());
        assertEquals(fecha, partida.getFecha());
        assertEquals("b", partida.getJugador1());
        assertEquals("c", partida.getJugador2());
        assertFalse(partida.isCompleta());
    }
    
    @Test
    void testConstructorIdNulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ResumenPartida(null, fecha, "b", "c");
        });
    }
    
    @Test
    void testConstructorIdVacio() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ResumenPartida("", fecha, "b", "c");
        });
    }
    
    @Test
    void testConstructorFechaNula() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ResumenPartida("Partida1", null, "b", "c");
        });
    }
    
    @Test
    void testConstructorJugador1Nulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ResumenPartida("Partida1", fecha, null, "c");
        });
    }
    
    @Test
    void testConstructorJugador1Vacio() {
        LocalDate fecha = LocalDate.of(2025, 11, 8);
        assertThrows(IllegalArgumentException.class, () -> {
            new ResumenPartida("Partida1", fecha, "", "Bob");
        });
    }
    
    @Test
    void testConstructorJugador2Nulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ResumenPartida("Partida1", fecha, "b", null);
        });
    }
    
    @Test
    void testConstructorJugador2Vacio() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ResumenPartida("Partida1", fecha, "b", "");
        });
    }


        @Test
    void testMarcarComoCompleta() {
        ResumenPartida partida = new ResumenPartida("Partida1", fecha, "b", "c");
        assertFalse(partida.isCompleta());
        partida.marcarComoCompleta();
        assertTrue(partida.isCompleta());
    }
    

    @Test
    void testGetGanadorPartidaNoCompleta() {
        ResumenPartida partida = new ResumenPartida("Partida1", fecha, "b", "c");
        assertThrows(IllegalStateException.class, () -> {
            partida.getGanador();
        });
    }
    

    @Test
    void testGetGanadorJugador1Gana() {
        ResumenPartida partida = new ResumenPartida("Partida1", fecha, "b", "c");
        Jugador jugador1 = new Jugador("b");
        Jugador jugador2 = new Jugador("c");
        
        partida.establecerResultados(jugador1, jugador2, 10, 5);
        partida.marcarComoCompleta();

        assertEquals("b", partida.getGanador());
    }

    @Test
    void testGetGanadorJugador2Gana() {
        ResumenPartida partida = new ResumenPartida("Partida1", fecha, "b", "c");
        Jugador jugador1 = new Jugador("b");
        Jugador jugador2 = new Jugador("c");
        
        partida.establecerResultados(jugador1, jugador2, 5, 10);
        partida.marcarComoCompleta();

        assertEquals("c", partida.getGanador());
    }

    @Test
    void testGetGanadorEmpate() {
        ResumenPartida partida = new ResumenPartida("Partida1", fecha, "b", "c");
        Jugador jugador1 = new Jugador("b");
        Jugador jugador2 = new Jugador("c");
        
        partida.establecerResultados(jugador1, jugador2, 7, 7);
        partida.marcarComoCompleta();

        assertNull(partida.getGanador());
    }
    

    
    @Test
    void testEstablecerResultadosValido() {
        ResumenPartida partida = new ResumenPartida("Partida1", fecha, "b", "c");
        Jugador jugador1 = new Jugador("a");
        Jugador jugador2 = new Jugador("b");
        
        jugador1.sumarEscoba();
        jugador1.sumarEscoba();
        jugador1.agregarCartaACartas(new Carta(Palo.OROS, 7));
        jugador1.agregarCartaACartas(new Carta(Palo.OROS, 1));
        jugador1.agregarCartaACartas(new Carta(Palo.COPAS, 7));
        
        jugador2.agregarCartaACartas(new Carta(Palo.ESPADAS, 7));
        
        partida.establecerResultados(jugador1, jugador2, 8, 3);
        
        assertEquals(2, partida.getEscobasJugador1());
        assertEquals(0, partida.getEscobasJugador2());
        assertEquals(3, partida.getCartasCapturadasJugador1());
        assertEquals(1, partida.getCartasCapturadasJugador2());
        assertEquals(2, partida.getOrosJugador1());
        assertEquals(0, partida.getOrosJugador2());
        assertEquals(2, partida.getSietesJugador1());
        assertEquals(1, partida.getSietesJugador2());
        assertTrue(partida.isGuindisJugador1());
        assertFalse(partida.isGuindisJugador2());
        assertEquals(8, partida.getPuntosJugador1());
        assertEquals(3, partida.getPuntosJugador2());
        assertTrue(partida.isCompleta());
    }
    
    @Test
    void testEstablecerResultadosJugador1Nulo() {
        ResumenPartida partida = new ResumenPartida("Partida1", fecha, "b", "c");
        Jugador jugador2 = new Jugador("b");
        
        assertThrows(IllegalArgumentException.class, () -> {
            partida.establecerResultados(null, jugador2, 8, 3);
        });
    }

    @Test
    void testEstablecerResultadosJugador2Nulo() {
        ResumenPartida partida = new ResumenPartida("Partida1", fecha, "b", "c");
        Jugador jugador1 = new Jugador("b");
        
        assertThrows(IllegalArgumentException.class, () -> {
            partida.establecerResultados(jugador1, null, 8, 3);
        });
    }

    @Test
    void testEqualsValidoMismoObjeto() {
        ResumenPartida partida = new ResumenPartida("Partida1", fecha, "b", "c");
        assertEquals(partida, partida);
    }

    @Test
    void testEqualsNoValidoObjetoDiferenteClase() {
        ResumenPartida partida = new ResumenPartida("Partida1", fecha, "b", "c");
        String otroObjeto = "";
        assertNotEquals(partida, otroObjeto);
    }

    @Test
    void testEqualsNoValidoIdsDiferentes() {
        ResumenPartida partida1 = new ResumenPartida("Partida1", fecha, "b", "c");
        ResumenPartida partida2 = new ResumenPartida("Partida2", fecha, "b", "c");
        assertNotEquals(partida1, partida2);
    }

    @Test
    void testEqualsValidoPartidasIguales() {
        ResumenPartida partida1 = new ResumenPartida("Partida1", fecha, "b", "c");
        ResumenPartida partida2 = new ResumenPartida("Partida1", fecha, "b", "c");
        assertEquals(partida1, partida2);
    }

    @Test
    void testEqualsValidoFechaDiferente() {
        ResumenPartida partida1 = new ResumenPartida("Partida1", fecha, "b", "c");
        ResumenPartida partida2 = new ResumenPartida("Partida1", LocalDate.of(2025, 8, 7), "b", "c");
        assertNotEquals(partida1, partida2);
    }

    @Test
    void testEqualsValidoJugador1Diferente() {
        ResumenPartida partida1 = new ResumenPartida("Partida1", fecha, "b", "c");
        ResumenPartida partida2 = new ResumenPartida("Partida1", fecha, "d", "c");
        assertNotEquals(partida1, partida2);
    }

    @Test
    void testEqualsValidoJugador2Diferente() {
        ResumenPartida partida1 = new ResumenPartida("Partida1", fecha, "b", "c");
        ResumenPartida partida2 = new ResumenPartida("Partida1", fecha, "b", "d");
        assertNotEquals(partida1, partida2);
    }

    @Test
    @Tag("Cobertura")
    void testEqualsValidoObjNull() {
        ResumenPartida partida1 = new ResumenPartida("Partida1", fecha, "b", "c");
        assertNotEquals(partida1, null);
    }

    @Test
    void testConstructorVacioValido() {
        ResumenPartida resumen = new ResumenPartida();
        assertNotNull(resumen);
        assertNull(resumen.getId());
        assertNull(resumen.getFecha());
        assertNull(resumen.getJugador1());
        assertNull(resumen.getJugador2());
        assertFalse(resumen.isCompleta());
    }

}