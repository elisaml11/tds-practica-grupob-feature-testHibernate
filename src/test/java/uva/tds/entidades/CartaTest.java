package uva.tds.entidades;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import uva.tds.ejecutores.AdaptadorCartaJson;

/**
 * Tests para la clase Carta
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class CartaTest {

    @Test
    void testCreacionCartaValidaLimiteInferior() {
        Carta carta = new Carta(Palo.BASTOS, 1);
        assertEquals(Palo.BASTOS, carta.getPalo());
        assertEquals(1, carta.getIndice());
    }

    @Test
    void testCreacionCartaValidaPaloEspadas() {
        Carta carta = new Carta(Palo.ESPADAS, 1);
        assertEquals(Palo.ESPADAS, carta.getPalo());
        assertEquals(1, carta.getIndice());
    }

    @Test
    void testCreacionCartaPaloNulo() {
        assertThrows(IllegalArgumentException.class, () -> new Carta(null, 5));
    }

    @Test
    void testCreacionCartaInvalidaIndiceMenorQueUno() {
        assertThrows(IllegalArgumentException.class, () -> new Carta(Palo.OROS, 0));
    }

    @Test
    void testCreacionCartaIndiceMayorQueSieteYMenorQueDiez() {
        assertThrows(IllegalArgumentException.class, () -> new Carta(Palo.ESPADAS, 8));
    }

    @Test
    void testCreacionCartaInvalidaIndiceMayorQueDoce() {
        assertThrows(IllegalArgumentException.class, () -> new Carta(Palo.COPAS, 13));
    }

    @Test
    void testCreacionCartaValidaLimiteSuperior() {
        Carta carta = new Carta(Palo.COPAS, 12);
        assertEquals(Palo.COPAS, carta.getPalo());
        assertEquals(12, carta.getIndice());
    }

    @Test
    void testConstructorVacioValido() {
        Carta carta = new Carta();
        assertNotNull(carta);
        assertNull(carta.getPalo());
        assertEquals(0, carta.getIndice());
    }

    @Test
    void testEqualsValido() {
        Carta carta1 = new Carta(Palo.OROS, 5);
        Carta carta2 = new Carta(Palo.OROS, 5);
        assertEquals(carta1, carta2);
    }

    @Test
    void testEqualsNoValidoDistintoPalo() {
        Carta carta1 = new Carta(Palo.BASTOS, 3);
        Carta carta2 = new Carta(Palo.ESPADAS, 3);
        assertEquals(false, carta1.equals(carta2));
    }

    @Test
    void testToStringValidoLimiteInferior() {
        Carta carta = new Carta(Palo.OROS, 1);
        assertEquals("1-oros", carta.toString());
    }

    @Test
    void testToStringValidoLimiteSuperior() {
        Carta carta = new Carta(Palo.ESPADAS, 12);
        assertEquals("12-espadas", carta.toString());
    }

    @Test
    void testFromStringValidoLimiteInferior() {
        Carta carta = AdaptadorCartaJson.parse("1-oros");
        assertEquals(Palo.OROS, carta.getPalo());
        assertEquals(1, carta.getIndice());
    }

    @Test
    void testFromStringValidoLimiteSuperior() {
        Carta carta = AdaptadorCartaJson.parse("12-espadas");
        assertEquals(Palo.ESPADAS, carta.getPalo());
        assertEquals(12, carta.getIndice());
    }

    @Test
    void testFromStringNoValidoLimiteInferior() {
        assertThrows(IllegalArgumentException.class, () -> AdaptadorCartaJson.parse("0-oros"));
    }

    @Test
    void testFromStringNoValidoLimiteSuperior() {
        assertThrows(IllegalArgumentException.class, () -> AdaptadorCartaJson.parse("13-espadas"));
    }

    @Test
    void testFromStringNoValidoIndiceMayorQueSieteYMenorQueDiez() {
        assertThrows(IllegalArgumentException.class, () -> AdaptadorCartaJson.parse("8-oros"));
    }

    @Test
    void testFromStringInvalidoNull() {
        assertThrows(IllegalArgumentException.class, () -> AdaptadorCartaJson.parse(null));
    }

    @Test
    void testFromStringInvalidoVacio() {
        assertThrows(IllegalArgumentException.class, () -> AdaptadorCartaJson.parse(""));
    }

    @Test
    void testFromStringInvalidoFormatoIncorrecto() {
        assertThrows(IllegalArgumentException.class, () -> AdaptadorCartaJson.parse("5espadas"));
    }
    @Test
    @Tag("Cobertura")
    void testFromStringNoValidoIndiceInvalidoNumeros() {
        assertThrows(IllegalArgumentException.class, () -> AdaptadorCartaJson.parse("198-oros"));
    }

    @Test
    @Tag("Cobertura")
    void testFromStringNoValidoIndiceInvalidoLetras() {
        assertThrows(IllegalArgumentException.class, () -> AdaptadorCartaJson.parse("abc-oros"));
    }

    @Test
    @Tag("Cobertura")
    void testFromStringNoValidoPaloInvalido() {
        assertThrows(IllegalArgumentException.class, () -> AdaptadorCartaJson.parse("5-diamantes"));
    }

    @Test
    void testEqualsValidoMismoObjeto(){
        Carta carta1 = new Carta(Palo.OROS, 5);
        assertTrue(carta1.equals(carta1));
    }

    @Test
    void testEqualsValidoMismaCarta(){
        Carta carta1 = new Carta(Palo.COPAS, 10);
        Carta carta2 = new Carta(Palo.COPAS, 10);
        assertTrue(carta1.equals(carta2));
    }

    @Test
    void testEqualsValidoNulo(){
        Carta carta1 = new Carta(Palo.ESPADAS, 3);
        assertFalse(carta1.equals(null));
    }

    @Test
    void testEqualsValidoDistintoTipo(){
        Carta carta1 = new Carta(Palo.BASTOS, 1);
        Object otraClase = "No es una carta";
        assertFalse(carta1.equals(otraClase));
    }

    @Test
    void testEqualsValidoDistintoPalo(){
        Carta carta1 = new Carta(Palo.BASTOS, 7);
        Carta carta2 = new Carta(Palo.OROS, 7);
        assertFalse(carta1.equals(carta2));
    }

    @Test
    void testEqualsValidoDistintonNumero(){
        Carta carta1 = new Carta(Palo.OROS, 7);
        Carta carta2 = new Carta(Palo.OROS, 6);
        assertFalse(carta1.equals(carta2));
    }
}
