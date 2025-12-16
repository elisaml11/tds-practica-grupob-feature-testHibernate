package uva.tds.servicios;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uva.tds.entidades.Carta;
import uva.tds.entidades.Jugador;
import uva.tds.entidades.Palo;

/**
 * Clase de test para GestorRonda.
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class GestorRondaTest {

    private GestorRonda gr;
    private Jugador j1;

    @BeforeEach
    void setUp() {
        gr = new GestorRonda(1);
        j1 = new Jugador("A");
    }

    @Test
    void testConstructorYNumeroInicial() {
        assertEquals(1, gr.getNumero());
        gr.incrementarNumero();
        assertEquals(2, gr.getNumero());
    }

    @Test
    void testSumaCartasIndicesEspeciales() {
        ArrayList<Carta> mesa = new ArrayList<>();
        mesa.add(new Carta(Palo.OROS, 12)); // valor 10
        mesa.add(new Carta(Palo.COPAS, 11)); // valor 9
        mesa.add(new Carta(Palo.BASTOS, 10)); // valor 8

        gr.setCartasMesa(mesa);

        assertEquals(27, gr.sumaCartas()); // 10 + 9 + 8 = 27
    }

    @Test
    void testSumaCartasConValoresNormalesYVacía() {
        assertEquals(0, gr.sumaCartas());

        ArrayList<Carta> mesa = new ArrayList<>();
        mesa.add(new Carta(Palo.ESPADAS, 1)); // 1
        mesa.add(new Carta(Palo.ESPADAS, 5)); // 5
        mesa.add(new Carta(Palo.COPAS, 7));   // 7

        gr.setCartasMesa(mesa);
        assertEquals(13, gr.sumaCartas()); // 1 + 5 + 7 = 13
    }

    @Test
    void testSetCartasMesaDevuelveCopiaYNoExponeReferenciaInterna() {
        ArrayList<Carta> inicial = new ArrayList<>();
        inicial.add(new Carta(Palo.OROS, 1));
        inicial.add(new Carta(Palo.COPAS, 3));
        gr.setCartasMesa(inicial);

        ArrayList<Carta> copia = gr.getCartasMesa();
        assertEquals(2, copia.size());
        copia.remove(0);
        assertEquals(2, gr.getCartasMesa().size());
    }

    @Test
    void testAnnadirYEliminarCartasMesaYEstaVacia() {
        Carta c1 = new Carta(Palo.BASTOS, 7);
        gr.annadeCartaMesa(c1);
        assertFalse(gr.estaVacia());
        ArrayList<Carta> eliminar = new ArrayList<>();
        eliminar.add(c1);
        gr.eliminaCartasMesa(eliminar);
        assertTrue(gr.estaVacia());
    }

    @Test
    void testEliminarrCartasMesaNullRemueveCorrectamente() {
        ArrayList<Carta> mesa = new ArrayList<>();
        Carta a = new Carta(Palo.OROS, 1);
        Carta b = new Carta(Palo.COPAS, 2);
        Carta c = new Carta(Palo.BASTOS, 3);
        mesa.add(a); mesa.add(b); mesa.add(c);

        gr.setCartasMesa(new ArrayList<>(mesa));

        // pasar null no debe lanzar ni cambiar la mesa
        gr.eliminaCartasMesa(null);
        assertEquals(3, gr.getCartasMesa().size());

        // eliminar una lista con B y C
        ArrayList<Carta> aEliminar = new ArrayList<>();
        aEliminar.add(b); aEliminar.add(c);
        gr.eliminaCartasMesa(aEliminar);

        ArrayList<Carta> restantes = gr.getCartasMesa();
        assertEquals(1, restantes.size());
        assertTrue(restantes.contains(a));
        assertFalse(restantes.contains(b));
        assertFalse(restantes.contains(c));
    }


    @Test
    void testAsignarCartasAJugadorVaciaMesa() {
        // preparar mesa con 2 cartas
        Carta c1 = new Carta(Palo.COPAS, 2);
        Carta c2 = new Carta(Palo.ESPADAS, 3);
        gr.annadeCartaMesa(c1);
        gr.annadeCartaMesa(c2);

        gr.asignarCartasAJugador(j1);
        assertTrue(j1.getCartas().contains(c1));
        assertTrue(j1.getCartas().contains(c2));
        assertTrue(gr.estaVacia());
    }

    @Test
    void testSetCartasMesaNullLimpiaMesa() {
        ArrayList<Carta> inicial = new ArrayList<>();
        inicial.add(new Carta(Palo.OROS, 1));
        gr.setCartasMesa(inicial);
        assertFalse(gr.estaVacia());
        gr.setCartasMesa(null);
        assertTrue(gr.estaVacia());
    }

    @Test
    void testAnnadeCartaMesaNulaLanza() {
        assertThrows(IllegalArgumentException.class, () -> gr.annadeCartaMesa(null));
    }

    @Test
    void testAsignarCartasAJugadorNuloLanza() {
        Carta c = new Carta(Palo.OROS, 4);
        gr.annadeCartaMesa(c);
        assertThrows(IllegalArgumentException.class, () -> gr.asignarCartasAJugador(null));
    }
}