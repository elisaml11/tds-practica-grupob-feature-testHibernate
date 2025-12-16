package uva.tds.entidades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Clase de test para la clase Turno.
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class TurnoTest {
    
    @Test
    void testConstructorValido() {
        Jugada jug = new Jugada(new Carta(Palo.COPAS, 1), null, null);
        Turno turno = new Turno(1, jug);
        assertEquals(1, turno.getNumero());
        assertEquals(jug, turno.getJugada());
    }

    @Test
    void testConstructorNoValidoNumeroMenorQue1() {
        Jugada jug = new Jugada(new Carta(Palo.BASTOS, 3), null, null);
        assertThrows(IllegalArgumentException.class, () -> new Turno(0, jug));
        assertThrows(IllegalArgumentException.class, () -> new Turno(-5, jug));
    }

    @Test
    void testConstructorNoValidoJugadaNula() {
        assertThrows(IllegalArgumentException.class, () -> new Turno(1, null));
    }

    @Test
    void testConstructorVacioValido() {
        Turno turno = new Turno();
        assertEquals(0, turno.getNumero());
        assertEquals(null, turno.getJugada());
    }
}
