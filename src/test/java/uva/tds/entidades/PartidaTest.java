package uva.tds.entidades;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Clase de test para la clase Partida.
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class PartidaTest {

    @Test
    void testValidoAñadirJugador1() {
       ArrayList<String> nombres = new ArrayList<>();
        Partida partida = new Partida();
        nombres.add("Ana");
        nombres.add("Luis");
        partida.añadirJugadores(nombres);
        assertEquals(nombres, partida.getNombres());
    }

    @Test
    void testNoValidoAñadirJugadores(){
        Partida partida = new Partida();
        assertThrows(IllegalArgumentException.class, () -> {
            partida.añadirJugadores(null);
        });
    }

    @Test
    void testValidoAñadirMesaInicial() {
        Partida partida = new Partida();
        ArrayList<Carta> mesa = new ArrayList<>();
        mesa.add(new Carta(Palo.OROS, 5));
        mesa.add(new Carta(Palo.COPAS, 10));
        partida.añadirMesaInicial(mesa);
        assertEquals(mesa, partida.getMesaInicial());
    }

    @Test
    void testNoValidoAñadirMesaInicialNula(){
        Partida partida = new Partida();
        assertThrows(IllegalArgumentException.class, () -> {
            partida.añadirMesaInicial(null);
        });
    }

    @Test
    void testValidoAñadirManoJugador1() {
        Partida partida = new Partida();
        ArrayList<ArrayList<Carta>> manos = new ArrayList<>();
        ArrayList<Carta> mano1=new ArrayList<>();
        mano1.add(new Carta(Palo.ESPADAS, 3));
        mano1.add(new Carta(Palo.BASTOS, 7));
        ArrayList<Carta> mano2=mano1;
        manos.add(mano1);
        manos.add(mano2);
        partida.añadirManoJugador1(manos);
        assertEquals(manos, partida.getManosJugador1());
    }

    @Test
    void testNoValidoAñadirManoJugador1Nula(){
        Partida partida = new Partida();
        assertThrows(IllegalArgumentException.class, () -> {
            partida.añadirManoJugador1(null);
        });
    }

    @Test
    void testValidoAñadirManoJugador2() {
        Partida partida = new Partida();
        ArrayList<ArrayList<Carta>> manos = new ArrayList<>();
        ArrayList<Carta> mano1=new ArrayList<>();
        mano1.add(new Carta(Palo.ESPADAS, 3));
        mano1.add(new Carta(Palo.BASTOS, 7));
        ArrayList<Carta> mano2=mano1;
        manos.add(mano1);
        manos.add(mano2);
        partida.añadirManoJugador2(manos);
        assertEquals(manos, partida.getManosJugador2());
    }

    @Test
    void testNoValidoAñadirManoJugador2Nula(){
        Partida partida = new Partida();
        assertThrows(IllegalArgumentException.class, () -> {
            partida.añadirManoJugador2(null);
        });
    }

    @Test
    void testValidoAñadirRondas() {
        Partida partida = new Partida();
        Carta juega = new Carta(Palo.OROS, 1);
        List<Carta> captura = new ArrayList<>();
        captura.add(new Carta(Palo.COPAS, 2));
        List<Carta> mesa = new ArrayList<>();
        mesa.add(new Carta(Palo.ESPADAS, 3));
        Jugada jugada = new Jugada(juega, captura, mesa);
        Turno turno = new Turno(1, jugada);
        List<Turno> turnosRonda = new ArrayList<>();
        turnosRonda.add(turno);
        Ronda ronda1 = new Ronda(1, turnosRonda);
        ArrayList<Ronda> rondas = new ArrayList<>();
        rondas.add(ronda1);
        partida.anadirRondas(rondas);
        assertEquals(rondas, partida.getRondas());
    }

    @Test
    void testNoValidoAñadirRondasNulas(){
        Partida partida = new Partida();
        assertThrows(IllegalArgumentException.class, () -> {
            partida.anadirRondas(null);
        });
    }
    
    @Test
    void testConstructorConIdFechaYJugadoresValido() {
        LocalDate fecha = LocalDate.of(2025, 11, 24);
        Partida partida = new Partida("P10", fecha, "Ana", "Luis");

        assertEquals("P10", partida.getId());
        assertEquals(fecha, partida.getFecha());
        assertEquals("Ana", partida.getJugador1());
        assertEquals("Luis", partida.getJugador2());
        assertEquals(2, partida.getNombres().size());
        assertEquals(false, partida.isCompleta());
    }

    @Test
    void testConstructorIdNullNoValido() {
        LocalDate fecha = LocalDate.of(2025, 11, 24);
        assertThrows(IllegalArgumentException.class, () -> new Partida(null, fecha, "A", "B"));
    }

    @Test
    void testConstructorIdVacioNoValido() {
        LocalDate fecha = LocalDate.of(2025, 11, 24);
        assertThrows(IllegalArgumentException.class, () -> new Partida("", fecha, "A", "B"));
    }

    @Test
    void testConstructorFechaNullNoValido() {
        assertThrows(IllegalArgumentException.class, () -> new Partida("P", null, "A", "B"));
    }

    @Test
    void testConstructorJugador1NoValidoNull() {
        LocalDate fecha = LocalDate.of(2025, 11, 24);
        assertThrows(IllegalArgumentException.class, () -> new Partida("P", fecha, null, "B"));
    }

    @Test
    @Tag("Cobertura")
    void testConstructorJugador1NoValidoVacio() {
        LocalDate fecha = LocalDate.of(2025, 11, 24);
        assertThrows(IllegalArgumentException.class, () -> new Partida("P", fecha, "", "B"));
    }

    @Test
    @Tag("Cobertura")
    void testConstructorJugador2NoValidoVacio() {
        LocalDate fecha = LocalDate.of(2025, 11, 24);
        assertThrows(IllegalArgumentException.class, () -> new Partida("P", fecha, "A", ""));
    }

    @Test
    void testConstructorJugador2NoValidoNull() {
        LocalDate fecha = LocalDate.of(2025, 11, 24);
        assertThrows(IllegalArgumentException.class, () -> new Partida("P", fecha, "A", null));
    }

    @Test
    void testGetIdNoValidoSinInicializar() {
        Partida partida = new Partida();
        assertThrows(IllegalStateException.class, () -> partida.getId());
    }

    @Test
    void testGetFechaNoValidoSinInicializar() {
        Partida partida = new Partida();
        assertThrows(IllegalStateException.class, () -> partida.getFecha());
    }

    @Test
    void testGetJugador1NoValidoSinInicializar() {
        Partida partida = new Partida();
        assertThrows(IllegalStateException.class, () -> partida.getJugador1());
    }

    @Test
    void testGetJugador2NoValidoSinInicializar() {
        Partida partida = new Partida();
        assertThrows(IllegalStateException.class, () -> partida.getJugador2());
    }

    @Test
    void testEstablecerResultadosNoValidoSinId() {
        Partida partida = new Partida();
        Jugador j1 = new Jugador("Ana");
        Jugador j2 = new Jugador("Luis");
        assertThrows(IllegalStateException.class, () -> partida.establecerResultados(j1, j2, 0, 1));
    }


    @Test
    void testEstablecerResultadosValido() {
        LocalDate fecha = LocalDate.of(2025, 11, 24);
        Partida partida = new Partida("P20", fecha, "Ana", "Luis");

        Jugador ana = new Jugador("Ana");
        Jugador luis = new Jugador("Luis");
        ana.agregarCartaACartas(new Carta(Palo.OROS, 1));
        luis.agregarCartaACartas(new Carta(Palo.OROS, 7)); // guindis
        luis.sumarEscoba();

        partida.establecerResultados(ana, luis, 2, 5);

        assertTrue(partida.isCompleta());
        assertEquals(2, partida.getPuntosJugador1());
        assertEquals(5, partida.getPuntosJugador2());
        assertEquals(0, partida.getEscobasJugador1());
        assertEquals(1, partida.getEscobasJugador2());
        assertEquals(1, partida.getOrosJugador1());
        assertEquals(1, partida.getOrosJugador2());
        assertEquals(0, partida.getSietesJugador1());
        assertEquals(1, partida.getSietesJugador2());
        assertFalse(partida.isGuindisJugador1());
        assertTrue(partida.isGuindisJugador2());
        assertEquals(1, partida.getCartasCapturadasJugador2());
        assertEquals("Luis", partida.getGanador());
    }

    @Test
    void testSetResumenPartidaNoValidoNull() {
        LocalDate fecha = LocalDate.of(2025, 11, 24);
        Partida partida = new Partida("P30", fecha, "Ana", "Luis");
        assertThrows(IllegalArgumentException.class, () -> partida.setResumenPartida(null));
    }

    @Test
    void testSetYGetResumenPartida() {
        LocalDate fecha = LocalDate.of(2025, 11, 24);
        Partida partida = new Partida("P40", fecha, "Ana", "Luis");
        ResumenPartida resumen = new ResumenPartida("P40", fecha, "Ana", "Luis");
        resumen.marcarComoCompleta();
        partida.setResumenPartida(resumen);
        assertEquals(resumen, partida.getResumenPartida());
        assertTrue(partida.isCompleta());
    }
    
    @Test
    void testGetCartasCapturadasJugador1Valido() {
        LocalDate fecha = LocalDate.of(2025, 11, 24);
        Partida partida = new Partida("P60", fecha, "Ana", "Luis");

        Jugador ana = new Jugador("Ana");
        Jugador luis = new Jugador("Luis");
        ana.agregarCartaACartas(new Carta(Palo.OROS, 1));
        ana.agregarCartaACartas(new Carta(Palo.COPAS, 2));
        luis.agregarCartaACartas(new Carta(Palo.OROS, 7));

        partida.establecerResultados(ana, luis, 0, 1);

        assertEquals(2, partida.getCartasCapturadasJugador1());
    }

    @Test
    @Tag("Cobertura")
    void testNoValidoRequiereResumen() {
        Partida partida = new Partida();
        assertThrows(IllegalStateException.class, () -> partida.getPuntosJugador1());
    }

    @Test
    @Tag("Cobertura")
    void testValidoGetMesaInicialJ1J2JugadasNulo(){
        LocalDate fecha = LocalDate.of(2025, 11, 24);
        Partida partida = new Partida("P60", fecha, "Ana", "Luis");
        assertTrue(partida.getMesaInicial().isEmpty());
        assertTrue(partida.getManosJugador1().isEmpty());
        assertTrue(partida.getManosJugador2().isEmpty());
        assertTrue(partida.getRondas().isEmpty());
    }

    @Test
    void testConstructorVacioValido() {
        Partida partida = new Partida();
        assertNotNull(partida);
        assertNotNull(partida.getNombres());
        assertTrue(partida.getNombres().isEmpty());
        assertFalse(partida.isCompleta());
    }

}
