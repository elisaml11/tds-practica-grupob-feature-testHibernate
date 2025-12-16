package uva.tds.entidades;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ManoTest {
    
    @Test
    void testConstructorManoValido() {
        Carta carta1 = new Carta(Palo.BASTOS, 3);
        Carta carta2 = new Carta(Palo.COPAS, 5);
        Carta carta3 = new Carta(Palo.ESPADAS, 7);
        List<Carta> cartas = List.of(carta1, carta2, carta3);

        Mano mano = new Mano(1, "Jugador1", cartas);

        assertEquals(1, mano.getNumeroRonda());
        assertEquals("Jugador1", mano.getNombreJugador());
        assertEquals(cartas, mano.getCartas());
    }

    @Test
    void testConstructorManoConCartasNulas() {
        Mano mano = new Mano(2, "Jugador2", null);

        assertEquals(2, mano.getNumeroRonda());
        assertEquals("Jugador2", mano.getNombreJugador());
        assertTrue(mano.getCartas().isEmpty());
    }

    @Test
    void testConstructorManoVacio() {
        Mano mano = new Mano();

        assertNotNull(mano);
        assertEquals(0, mano.getNumeroRonda());
        assertNull(mano.getNombreJugador());
        assertTrue(mano.getCartas().isEmpty());
    }

    @Test
    void testGetIdValido(){
        Mano mano = new Mano();
        assertNull(mano.getId());
    }

    

    
}
