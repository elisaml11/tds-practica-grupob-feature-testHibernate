package uva.tds.entidades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * Tests para la clase Baraja
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class BarajaTest {

    @Test
    void testCreacionBarajaManualIgualConstructor() {
        Baraja barajaConstructor = new Baraja();
        
        ArrayList<Carta> cartas = new ArrayList<>();
                for (Palo palo : Palo.values()) {
                    for (int i = 1; i <= 7; i++) {
                        cartas.add(new Carta(palo, i));
                    }
                    for (int i = 10; i <= 12; i++) {
                        cartas.add(new Carta(palo, i));
                    }
                }

        assertEquals(cartas, barajaConstructor.getCartas());
    }

    @Test
    void testGetNumeroDeCartasValido(){
        Baraja baraja= new Baraja();
        assertEquals(40, baraja.getNumeroDeCartas());
    }

    @Test
    void testRobarCartaValido() {
        Baraja baraja = new Baraja();
        Carta asOros = new Carta(Palo.OROS, 1);
        baraja.robarCarta(asOros);
        assertEquals(false, baraja.getCartas().contains(asOros));
    }

    @Test
    void testRobarCartaNoValidoCartaNula() {
        Baraja baraja = new Baraja();
        assertThrows(IllegalArgumentException.class, () -> baraja.robarCarta(null));
    }

    @Test
    void testRobarCartaNoValidoCartaNoEnBaraja() {
        Baraja baraja = new Baraja();
        Carta asOros = new Carta(Palo.OROS, 1); 
        baraja.robarCarta(asOros); 
        assertThrows(IllegalArgumentException.class, () -> baraja.robarCarta(asOros));
    }

    @Test
    void testRobarCartaNoValidaBarajaSinCartas(){
        Baraja baraja = new Baraja();
        for (Carta carta : baraja.getCartas()) {
            baraja.robarCarta(carta);
        }
        Carta cartaInexistente = new Carta(Palo.OROS, 1);
        assertThrows(IllegalStateException.class, () -> baraja.robarCarta(cartaInexistente));
    }

 
}
