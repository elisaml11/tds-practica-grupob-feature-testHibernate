package uva.tds.servicios;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uva.tds.entidades.Jugador;
/**
 * Clase de test para GestorTurno.
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class GestorTurnoTest {

    private Jugador j1;
    private Jugador j2;
    private Jugador j3;

    @BeforeEach
    void setUp() {
        j1 = new Jugador("A");
        j2 = new Jugador("B");
        j3 = new Jugador("C");
    }

    @Test
    void testConstructorNoValidoListaNula() {
        assertThrows(IllegalArgumentException.class, () -> new GestorTurno(null, 6));
    }

    @Test
    void testConstructorNoValidoMenosDeDosJugadores() {
        ArrayList<Jugador> soloUno = new ArrayList<>();
        soloUno.add(j1);
        assertThrows(IllegalArgumentException.class, () -> new GestorTurno(soloUno, 6));
    }

    @Test
    void testConstructorNoValidoTurnosPorRondaNoValido() {
        ArrayList<Jugador> dos = new ArrayList<>();
        dos.add(j1); dos.add(j2);
        assertThrows(IllegalArgumentException.class, () -> new GestorTurno(dos, 0));
        assertThrows(IllegalArgumentException.class, () -> new GestorTurno(dos, -1));
    }

    @Test
    void testIniciarConEstableceJugadorActual() {
        ArrayList<Jugador> dos = new ArrayList<>();
        dos.add(j1); dos.add(j2);
        GestorTurno gt = new GestorTurno(dos, 6);

        gt.iniciarCon(j2);
        assertEquals(j2, gt.getJugadorActual());

        GestorTurno gt2 = new GestorTurno(dos, 6);
        gt2.iniciarCon(new Jugador("X"));
        assertEquals(j1, gt2.getJugadorActual());
    }

    @Test
    void testIniciarConNullColocaJugadorPorDefecto() {
        ArrayList<Jugador> dos = new ArrayList<>();
        dos.add(j1); dos.add(j2);
        GestorTurno gt = new GestorTurno(dos, 6);

        // Si se pasa null, debe colocarse el primer jugador (índice 0)
        gt.iniciarCon(null);
        assertEquals(j1, gt.getJugadorActual());
        // además debe resetearse el contador de turnos jugados
        assertEquals(0, gt.getTurnosJugados());
    }
    
    @Test
    void testCambiarTurnoAlternaYCuentaTurnos() {
        ArrayList<Jugador> dos = new ArrayList<>();
        dos.add(j1); dos.add(j2);
        GestorTurno gt = new GestorTurno(dos, 6);
        gt.iniciarCon(j1);
        assertEquals(j1, gt.getJugadorActual());
        assertEquals(0, gt.getTurnosJugados());

        gt.cambiarTurno();
        assertEquals(j2, gt.getJugadorActual());
        assertEquals(1, gt.getTurnosJugados());

        gt.cambiarTurno();
        assertEquals(j1, gt.getJugadorActual());
        assertEquals(2, gt.getTurnosJugados());
    }

    @Test
    void testRondaTerminadaCuandoSeAlcanzanLosTurnosPorRonda() {
        int turnosPorRonda = 4;
        ArrayList<Jugador> dos = new ArrayList<>();
        dos.add(j1); dos.add(j2);
        GestorTurno gt = new GestorTurno(dos, turnosPorRonda);
        gt.iniciarCon(j1);
        for (int i = 0; i < turnosPorRonda; i++) {
            gt.cambiarTurno();
        }
        assertTrue(gt.rondaTerminada());
        assertEquals(turnosPorRonda, gt.getTurnosJugados());
    }

    @Test
    void testReiniciarTurnosReseteaContadorPeroNoJugador() {
        ArrayList<Jugador> tres = new ArrayList<>();
        tres.add(j1); tres.add(j2); tres.add(j3);
        GestorTurno gt = new GestorTurno(tres, 6);
        gt.iniciarCon(j2);
        gt.cambiarTurno(); // 1
        gt.cambiarTurno(); // 2
        assertEquals(2, gt.getTurnosJugados());
        Jugador antes = gt.getJugadorActual();
        gt.reiniciarTurnos();
        assertEquals(0, gt.getTurnosJugados());
        // el jugador actual conserva su posición lógica (no forzamos cambio de primer jugador)
        assertEquals(antes, gt.getJugadorActual());
    }

    @Test
    void testSoportaMasDeDosJugadoresYCicloCircular() {
        ArrayList<Jugador> tres = new ArrayList<>();
        tres.add(j1); tres.add(j2); tres.add(j3);
        GestorTurno gt = new GestorTurno(tres, 6);
        gt.iniciarCon(j1);
        gt.cambiarTurno(); // j2
        gt.cambiarTurno(); // j3
        gt.cambiarTurno(); // j1
        assertEquals(j1, gt.getJugadorActual());
        assertEquals(3, gt.getTurnosJugados());
    }

    @Test
    void testEstablecerJugadorActualNullNoCambia() {
        ArrayList<Jugador> dos = new ArrayList<>();
        dos.add(j1); dos.add(j2);
        GestorTurno gt = new GestorTurno(dos, 6);
        gt.iniciarCon(j2);

        gt.establecerJugadorActual(null);

        // No debe cambiar el jugador actual ni el contador de turnos
        assertEquals(j2, gt.getJugadorActual());
        assertEquals(0, gt.getTurnosJugados());
    }

    @Test
    void testEstablecerJugadorActualJugadorNoEnListaNoCambia() {
        ArrayList<Jugador> dos = new ArrayList<>();
        dos.add(j1); dos.add(j2);
        GestorTurno gt = new GestorTurno(dos, 6);
        gt.iniciarCon(j1);

        // Intentar establecer un jugador que no está en la lista
        gt.establecerJugadorActual(new Jugador("X"));

        assertEquals(j1, gt.getJugadorActual());
    }

    @Test
    void testEstablecerJugadorActualJugadorEnListaCambia() {
        ArrayList<Jugador> tres = new ArrayList<>();
        tres.add(j1); tres.add(j2); tres.add(j3);
        GestorTurno gt = new GestorTurno(tres, 6);

        gt.establecerJugadorActual(j3);
        assertEquals(j3, gt.getJugadorActual());
    }

    @Test
    void testGetJugadoresDevuelveCopiaIndependiente() {
        ArrayList<Jugador> dos = new ArrayList<>();
        dos.add(j1); dos.add(j2);
        GestorTurno gt = new GestorTurno(dos, 6);

        ArrayList<Jugador> listaDevuelta = gt.getJugadores();
        int tamAntes = listaDevuelta.size();

        // Modificamos la lista devuelta y comprobamos que la lista interna no cambia
        listaDevuelta.remove(0);
        ArrayList<Jugador> listaDespues = gt.getJugadores();
        assertEquals(tamAntes, listaDespues.size());
    }
}
