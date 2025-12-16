package uva.tds.entidades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Clase de test para la clase Jugada.
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class JugadaTest {

    @Test
    void testConstructorJugadaValido() {
         Carta juega = new Carta(Palo.OROS, 1);
        List<Carta> captura = new ArrayList<>();
        captura.add(new Carta(Palo.COPAS, 2));
        List<Carta> mesa = new ArrayList<>();
        mesa.add(new Carta(Palo.ESPADAS, 3));

        Jugada jug = new Jugada(juega, captura, mesa);

        assertEquals(juega, jug.getJuega());
        assertEquals(1, jug.getCaptura().size());
        assertEquals(new Carta(Palo.COPAS, 2), jug.getCaptura().get(0));
        assertEquals(1, jug.getMesaResultante().size());
        assertEquals(new Carta(Palo.ESPADAS, 3), jug.getMesaResultante().get(0));
    }

    @Test
    void testConstructorJugadaNoValidoCartaJuegaNula() {
        List<Carta> captura = new ArrayList<>();
        captura.add(new Carta(Palo.COPAS, 2));
        List<Carta> mesa = new ArrayList<>();
        mesa.add(new Carta(Palo.ESPADAS, 3));

        assertThrows(IllegalArgumentException.class, () -> {
            new Jugada(null, captura, mesa);
        });
    }

    @Test
    void testConstructorJugadaListasNulas() {
        Carta juega = new Carta(Palo.OROS, 1);

        Jugada jug = new Jugada(juega, null, null);

        assertEquals(juega, jug.getJuega());
        assertEquals(0, jug.getCaptura().size());
        assertEquals(0, jug.getMesaResultante().size());
    }

    @Test
    void testConstructorJugadaVacioValido() {
        Jugada jugada = new Jugada();
        assertEquals(null, jugada.getJuega());
        assertEquals(0, jugada.getCaptura().size());
        assertEquals(0, jugada.getMesaResultante().size());
    }

}
