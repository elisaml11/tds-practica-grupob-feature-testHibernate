package uva.tds.ejecutores;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import uva.tds.entidades.Carta;
import uva.tds.entidades.Palo;


public class AdaptadorCartaJsonTest {

    @Test
    void testParseValidoLimiteInferior() {
        Carta c = AdaptadorCartaJson.parse("1-oros");
        assertEquals(Palo.OROS, c.getPalo());
        assertEquals(1, c.getIndice());
    }

    @Test
    void testParseValidoLimiteSuperior() {
        Carta c = AdaptadorCartaJson.parse("12-espadas");
        assertEquals(Palo.ESPADAS, c.getPalo());
        assertEquals(12, c.getIndice());
    }

    @Test
    void testParseValidoValoresEspeciales() {
        Carta c10 = AdaptadorCartaJson.parse("10-copas");
        Carta c11 = AdaptadorCartaJson.parse("11-copas");
        Carta c12 = AdaptadorCartaJson.parse("12-copas");
        assertEquals(10, c10.getIndice());
        assertEquals(11, c11.getIndice());
        assertEquals(12, c12.getIndice());
        assertEquals(Palo.COPAS, c10.getPalo());
    }

    @Test
    void testParseNoValidoNull() {
        assertThrows(IllegalArgumentException.class, () -> AdaptadorCartaJson.parse(null));
    }

    @Test
    void testParseNoValidoVacio() {
        assertThrows(IllegalArgumentException.class, () -> AdaptadorCartaJson.parse(""));
    }

    @Test
    void testParseNoValidoFormatoSinSeparador() {
        assertThrows(IllegalArgumentException.class, () -> AdaptadorCartaJson.parse("5espadas"));
    }

    @Test
    void testParseNoValidoIndiceCero() {
        assertThrows(IllegalArgumentException.class, () -> AdaptadorCartaJson.parse("0-oros"));
    }

    @Test
    void testParseNoValidoIndiceEntre8y9() {
        assertThrows(IllegalArgumentException.class, () -> AdaptadorCartaJson.parse("8-oros"));
        assertThrows(IllegalArgumentException.class, () -> AdaptadorCartaJson.parse("9-oros"));
    }

    @Test
    void testParseNoValidoIndiceMayorQueDoce() {
        assertThrows(IllegalArgumentException.class, () -> AdaptadorCartaJson.parse("13-oros"));
        assertThrows(IllegalArgumentException.class, () -> AdaptadorCartaJson.parse("198-oros"));
    }

    @Test
    void testParseNoValidoIndiceNoNumerico() {
        assertThrows(IllegalArgumentException.class, () -> AdaptadorCartaJson.parse("abc-oros"));
    }

    @Test
    void testParseNoValidoPaloInvalido() {
        assertThrows(IllegalArgumentException.class, () -> AdaptadorCartaJson.parse("5-diamantes"));
    }
}