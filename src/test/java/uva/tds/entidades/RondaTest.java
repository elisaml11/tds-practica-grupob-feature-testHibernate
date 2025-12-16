package uva.tds.entidades;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Clase de test para la clase Ronda.
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class RondaTest {

    @Test
    void testConstructorValidoNumeroRondaLimiteInferior() {
        List<Turno> turnos = new ArrayList<>();
        turnos.add(new Turno(1, new Jugada(new Carta(Palo.OROS, 1), null, null)));
        turnos.add(new Turno(2, new Jugada(new Carta(Palo.COPAS, 2), null, null)));

        Ronda ronda = new Ronda(1, turnos);
        assertEquals(1, ronda.getNumero());
        assertEquals(2, ronda.getTurnos().size());
    }

    @Test
    void testConstructorNoValidoNumeroRondaLimiteInferior() {
        assertThrows(IllegalArgumentException.class, () -> new Ronda(0, new ArrayList<>()));
        assertThrows(IllegalArgumentException.class, () -> new Ronda(-1, null));
    }

    @Test
    void testConstructorAceptaTurnosNulosComoListaVacia() {
        Ronda ronda = new Ronda(2, null);
        assertNotNull(ronda.getTurnos());
        assertTrue(ronda.getTurnos().isEmpty());

        List<Turno> obtenido = ronda.getTurnos();
        obtenido.add(new Turno(1, new Jugada(new Carta(Palo.BASTOS, 7), null, null)));
        assertTrue(ronda.getTurnos().isEmpty());
    }

    @Test
    void testConstructorVacioValido() {
        Ronda ronda = new Ronda();
        assertNotNull(ronda);
        assertEquals(0, ronda.getNumero());
        assertNotNull(ronda.getTurnos());
        assertTrue(ronda.getTurnos().isEmpty());
    }

}