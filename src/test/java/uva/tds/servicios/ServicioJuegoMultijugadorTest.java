package uva.tds.servicios;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import uva.tds.ejecutores.AdaptadorCartaJson;
import uva.tds.entidades.Carta;
import uva.tds.entidades.Jugador;
import uva.tds.interfaces.ServicioMultijugador;

/**
 * Tests de la clase ServicioJuegoMultijugador
 * 
 * @author Marta Pérez Alonso
 * @author Elisa Martínez Lafuente
 */
public class ServicioJuegoMultijugadorTest {

    @TestSubject
    private ServicioJuegoMultijugador servicioJuego;

    @Mock
    private ServicioMultijugador multijugador;

    private final String ID_SALA_VALIDO = "SALA-001";
    private final String CREADOR_VALIDO = "Ana";
    private final String JUGADOR2_VALIDO = "Luis";
    private final String CODIGO_PRIVADA = "ABCD123";

    @BeforeEach
    void setUp() {
        multijugador = EasyMock.mock(ServicioMultijugador.class);
        servicioJuego = new ServicioJuegoMultijugador(multijugador);
    }

    // ========== TESTS CONSTRUCTOR ==========

    @Test
    void testConstructorNoValidoServicioNulo() {
        assertThrows(IllegalArgumentException.class, () -> new ServicioJuegoMultijugador(null));
    }

    @Test
    void testConstructorValido() {
        ServicioMultijugador mock = EasyMock.mock(ServicioMultijugador.class);
        ServicioJuegoMultijugador servicio = new ServicioJuegoMultijugador(mock);
        assertEquals(mock, servicio.getServicio());
    }

    // ========== TESTS CREAR PARTIDA ==========

    @Test
    void testCrearPartidaValidoPublica() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);

        verify(multijugador);
    }

    @Test
    void testCrearPartidaValidoPrivada() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, true);
        EasyMock.expectLastCall();
        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, true);

        verify(multijugador);
    }

    @Test
    void testCrearPartidaNoValidoIdSalaNulo() {
        replay(multijugador);
        assertThrows(IllegalArgumentException.class, () -> servicioJuego.crearPartida(null, CREADOR_VALIDO, false));
        verify(multijugador);
    }

    @Test
    void testCrearPartidaNoValidoIdSalaVacio() {
        replay(multijugador);
        assertThrows(IllegalArgumentException.class, () -> servicioJuego.crearPartida("", CREADOR_VALIDO, false));
        verify(multijugador);
    }

    @Test
    void testCrearPartidaNoValidoCreadorNulo() {
        replay(multijugador);
        assertThrows(IllegalArgumentException.class, () -> servicioJuego.crearPartida(ID_SALA_VALIDO, null, false));
        verify(multijugador);
    }

    @Test
    void testCrearPartidaNoValidoCreadorVacio() {
        replay(multijugador);
        assertThrows(IllegalArgumentException.class, () -> servicioJuego.crearPartida(ID_SALA_VALIDO, "", false));
        verify(multijugador);
    }

    // ========== TESTS UNIRSE A SALA ==========

    @Test
    void testUnirseASalaValidoPublica() {
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall();
        replay(multijugador);

        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);

        verify(multijugador);
    }

    @Test
    void testUnirseASalaValidoPrivadaConCodigo() {
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, CODIGO_PRIVADA);
        EasyMock.expectLastCall();
        replay(multijugador);

        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, CODIGO_PRIVADA);

        verify(multijugador);
    }

    @Test
    void testUnirseASalaNoValidoIdSalaNulo() {
        replay(multijugador);
        assertThrows(IllegalArgumentException.class, () -> servicioJuego.unirseASala(null, JUGADOR2_VALIDO, null));
        verify(multijugador);
    }

    @Test
    void testUnirseASalaNoValidoIdSalaVacio() {
        replay(multijugador);
        assertThrows(IllegalArgumentException.class, () -> servicioJuego.unirseASala("", JUGADOR2_VALIDO, null));
        verify(multijugador);
    }

    @Test
    void testUnirseASalaNoValidoNombreJugadorNulo() {
        replay(multijugador);
        assertThrows(IllegalArgumentException.class, () -> servicioJuego.unirseASala(ID_SALA_VALIDO, null, null));
        verify(multijugador);
    }

    @Test
    void testUnirseASalaNoValidoNombreJugadorVacio() {
        replay(multijugador);
        assertThrows(IllegalArgumentException.class, () -> servicioJuego.unirseASala(ID_SALA_VALIDO, "", null));
        verify(multijugador);
    }

    @Test
    void testUnirseASalaNoValidoSalaNoExiste() {
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall().andThrow(new IllegalStateException("Sala no existe"));
        replay(multijugador);

        assertThrows(IllegalStateException.class,
                () -> servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null));

        verify(multijugador);
    }

    @Test
    void testUnirseASalaNoValidoCodigoIncorrecto() {
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, "CODIGO_FALSO");
        EasyMock.expectLastCall().andThrow(new IllegalStateException("Código incorrecto"));
        replay(multijugador);

        assertThrows(IllegalStateException.class,
                () -> servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, "CODIGO_FALSO"));

        verify(multijugador);
    }

    @Test
    void testUnirseASalaNoValidoSalaLlena() {
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall().andThrow(new IllegalStateException("Sala llena"));
        replay(multijugador);

        assertThrows(IllegalStateException.class,
                () -> servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null));

        verify(multijugador);
    }

    @Tag("Cobertura")
    @Test
    void testUnirseASalaCuandoIdLocalDiferenteReiniciaEstadoYPermiteInicializar() {
        multijugador.crearSala("ID-SALA-VIEJO", CREADOR_VALIDO, false);
        expectLastCall();

        multijugador.unirseASala("ID-SALA-NUEVO", "X", null);
        expectLastCall();

        multijugador.unirseASala("ID-SALA-NUEVO", JUGADOR2_VALIDO, null);
        expectLastCall();

        replay(multijugador);
        servicioJuego.crearPartida("ID-SALA-VIEJO", CREADOR_VALIDO, false);
        servicioJuego.unirseASala("ID-SALA-NUEVO", "X", null);
        servicioJuego.unirseASala("ID-SALA-NUEVO", JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();

        assertEquals("X", servicioJuego.getGestorPartida().getJugador1().getNombre());
        assertEquals(JUGADOR2_VALIDO, servicioJuego.getGestorPartida().getJugador2().getNombre());

        verify(multijugador);
    }

    @Tag("Cobertura")
    @Test
    void testUnirseASalaValidoNoDuplicaJugadorExistente() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, CREADOR_VALIDO, null);
        expectLastCall();

        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        expectLastCall();

        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, CREADOR_VALIDO, null);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();
        assertEquals(CREADOR_VALIDO, servicioJuego.getGestorPartida().getJugador1().getNombre());
        assertEquals(JUGADOR2_VALIDO, servicioJuego.getGestorPartida().getJugador2().getNombre());

        verify(multijugador);
    }

    // ========== TESTS LISTAR SALAS ==========

    @Test
    void testListarSalasValidoDevuelveLista() {
        List<String> salas = List.of("SALA-001", "SALA-002", "SALA-003");
        expect(multijugador.listarSalasDisponibles()).andReturn(salas);
        replay(multijugador);

        List<String> resultado = servicioJuego.listarSalasDisponibles();

        assertEquals(3, resultado.size());
        verify(multijugador);
    }

    @Test
    void testListarSalasValidoDevuelveListaVacia() {
        List<String> salas = List.of();
        expect(multijugador.listarSalasDisponibles()).andReturn(salas);
        replay(multijugador);

        assertTrue(servicioJuego.listarSalasDisponibles().isEmpty());

        verify(multijugador);
    }

    // ========== TESTS INICIALIZAR PARTIDA ==========

    @Test
    void testInicializarPartidaValido() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall();
        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();

        assertEquals(CREADOR_VALIDO, servicioJuego.getGestorPartida().getJugador1().getNombre());
        assertEquals(JUGADOR2_VALIDO, servicioJuego.getGestorPartida().getJugador2().getNombre());
        verify(multijugador);
    }

    @Test
    void testInicializarPartidaNoValidoUnJugador() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);

        assertThrows(IllegalStateException.class, () -> servicioJuego.inicializarPartida());

        verify(multijugador);
    }

    // ========== TESTS AVANZAR RONDA ==========

    @Test
    void testAvanzarRondaNoValidoPartidaNoInicializada() {
        replay(multijugador);
        assertThrows(IllegalStateException.class, () -> servicioJuego.avanzarRonda());
        verify(multijugador);
    }

    @Test
    void testAvanzarRondaValidoAvanza() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall();
        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();

        servicioJuego.avanzarRonda();
        assertEquals(2, servicioJuego.getGestorPartida().getRondaActual());

        verify(multijugador);
    }

    // ========== TESTS FINALIZAR PARTIDA ==========

    @Test
    void testFinalizarPartidaNoValidoPartidaNoInicializada() {
        replay(multijugador);
        assertThrows(IllegalStateException.class, () -> servicioJuego.finalizarPartida());
        verify(multijugador);
    }

    @Test
    void testFinalizarPartidaValidoFinaliza() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall();
        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();

        servicioJuego.finalizarPartida();
        assertEquals(-1, servicioJuego.getGestorPartida().getRondaActual());

        verify(multijugador);
    }

    // ========== TESTS REPARTIR CARTAS INICIAL ==========

    @Test
    void testRepartirCartasInicialValido() {
        // crear sala, poner jugadores, inicializar partida
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall();

        // para reparto inicial
        List<String> cartasMesa = List.of("5-oros", "11-oros", "10-bastos", "11-bastos");
        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");

        multijugador.enviarCartasDeMesa(ID_SALA_VALIDO, CREADOR_VALIDO, cartasMesa);
        EasyMock.expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, CREADOR_VALIDO, cartasAna);
        EasyMock.expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, JUGADOR2_VALIDO, cartasLuis);
        EasyMock.expectLastCall();

        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();
        servicioJuego.repartirCartasInicial(cartasMesa, cartasAna, cartasLuis);

        verify(multijugador);
    }

    @Test
    void testRepartirCartasInicialNoValidoPartidaNoInicializada() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);

        List<String> cartasMesa = List.of("5-oros", "11-oros", "10-bastos", "11-bastos");
        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");

        assertThrows(IllegalStateException.class,
                () -> servicioJuego.repartirCartasInicial(cartasMesa, cartasAna, cartasLuis));

        verify(multijugador);
    }

    @Test
    void testRepartirCartasInicialNoValidoCartasMesaNulas() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall();
        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();

        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");

        assertThrows(IllegalArgumentException.class,
                () -> servicioJuego.repartirCartasInicial(null, cartasAna, cartasLuis));

        verify(multijugador);
    }

    @Test
    void testRepartirCartasInicialNoValidoNumeroIncorrecto() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall();
        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();

        List<String> cartasMesa = List.of("5-oros", "11-oros"); // Faltan cartas
        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");

        assertThrows(IllegalArgumentException.class,
                () -> servicioJuego.repartirCartasInicial(cartasMesa, cartasAna, cartasLuis));

        verify(multijugador);
    }

    // ========== TESTS REPARTIR CARTAS RONDA ==========

    @Test
    void testRepartirCartasRondaValido() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall();

        List<String> cartasAna = List.of("2-espadas", "5-espadas", "10-espadas");
        List<String> cartasLuis = List.of("4-espadas", "6-oros", "6-bastos");

        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, CREADOR_VALIDO, cartasAna);
        EasyMock.expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, JUGADOR2_VALIDO, cartasLuis);
        EasyMock.expectLastCall();

        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();

        // Avanzar de ronda para permitir reparto de ronda (no se puede repartir en la
        // ronda 1)
        servicioJuego.avanzarRonda();

        // Ahora el reparto de ronda debe funcionar y las llamadas al mock deben
        // producirse
        servicioJuego.repartirCartasRonda(cartasAna, cartasLuis);

        verify(multijugador);
    }

    @Test
    void testRepartirCartasRondaNoValidoCartasNulasJugador1() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall();
        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();

        List<String> cartasLuis = List.of("4-espadas", "6-oros", "6-bastos");

        assertThrows(IllegalArgumentException.class, () -> servicioJuego.repartirCartasRonda(null, cartasLuis));

        verify(multijugador);
    }

    @Tag("Cobertura")
    @Test
    void testRepartirCartasRondaNoValidoCartasNulasJugador2() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall();
        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();

        List<String> cartasLuis = List.of("4-espadas", "6-oros", "6-bastos");

        assertThrows(IllegalArgumentException.class, () -> servicioJuego.repartirCartasRonda(cartasLuis, null));

        verify(multijugador);
    }

    @Tag("Cobertura")
    @Test
    void testRepartirCartasRondaNoValidoCartasDeMasJugador1() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall();
        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();

        List<String> cartasLuis = List.of("4-espadas", "6-oros", "6-bastos", "7-copas");
        List<String> cartasAna = List.of("2-espadas", "5-espadas", "10-espadas");

        assertThrows(IllegalArgumentException.class, () -> servicioJuego.repartirCartasRonda(cartasLuis, cartasAna));

        verify(multijugador);
    }

    @Tag("Cobertura")
    @Test
    void testRepartirCartasRondaNoValidoCartasDeMasJugador2() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall();
        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();

        List<String> cartasLuis = List.of("4-espadas", "6-oros", "6-bastos", "7-copas");
        List<String> cartasAna = List.of("2-espadas", "5-espadas", "10-espadas");

        assertThrows(IllegalArgumentException.class, () -> servicioJuego.repartirCartasRonda(cartasAna, cartasLuis));

        verify(multijugador);
    }

    @Test
    void testRepartirCartasRondaNoValidoNoCreador() throws Exception {
        ServicioJuegoMultijugador servicioCreador = new ServicioJuegoMultijugador(multijugador);
        ServicioJuegoMultijugador servicioOtro = new ServicioJuegoMultijugador(multijugador);

        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        expectLastCall();

        replay(multijugador);

        servicioCreador.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioCreador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioCreador.inicializarPartida();

        // Intentamos repartir desde servicioOtro (no creador):
        List<String> cartasAna = List.of("2-espadas", "5-espadas", "10-espadas");
        List<String> cartasLuis = List.of("4-espadas", "6-oros", "6-bastos");

        assertThrows(IllegalStateException.class, () -> servicioOtro.repartirCartasRonda(cartasAna, cartasLuis));

        verify(multijugador);
    }

    @Tag("Cobertura")
    @Test
    void testRepartirCartasInicialNoValidoCartasDeMas() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        expectLastCall();
        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();

        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas", "3-bastos");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");
        List<String> mesa = List.of("5-oros", "11-oros", "10-bastos", "11-bastos");
        List<String> mesaCorta = List.of("5-oros", "11-oros", "10-bastos");
        assertThrows(IllegalArgumentException.class,
                () -> servicioJuego.repartirCartasInicial(mesa, cartasAna, cartasLuis));
        assertThrows(IllegalArgumentException.class,
                () -> servicioJuego.repartirCartasInicial(mesa, cartasLuis, cartasAna));
        assertThrows(IllegalArgumentException.class,
                () -> servicioJuego.repartirCartasInicial(mesaCorta, cartasAna, cartasLuis));
        verify(multijugador);
    }

    @Test
    void testRepartirCartasInicialNoValidoNulos() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        expectLastCall();
        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();

        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");
        List<String> mesa = List.of("5-oros", "11-oros", "10-bastos", "11-bastos");
        assertThrows(IllegalArgumentException.class,
                () -> servicioJuego.repartirCartasInicial(null, cartasAna, cartasLuis));
        assertThrows(IllegalArgumentException.class,
                () -> servicioJuego.repartirCartasInicial(cartasLuis, null, cartasLuis));
        assertThrows(IllegalArgumentException.class, () -> servicioJuego.repartirCartasInicial(mesa, cartasAna, null));
        verify(multijugador);
    }

    // ========== TESTS REALIZAR JUGADA ==========

    @Tag("Cobertura")
    @Test
    void testRealizarJugadaNoValidoCartasACapturarNull() {
        // preparar sala y reparto inicial válido (mock expectations)
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        expectLastCall();

        List<String> cartasMesa = List.of("5-oros", "11-oros", "10-bastos", "11-bastos");
        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");

        multijugador.enviarCartasDeMesa(ID_SALA_VALIDO, CREADOR_VALIDO, cartasMesa);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, CREADOR_VALIDO, cartasAna);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, JUGADOR2_VALIDO, cartasLuis);
        expectLastCall();

        // llamadas de enviar jugada y capturas no se esperan porque fallará antes
        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();
        servicioJuego.repartirCartasInicial(cartasMesa, cartasAna, cartasLuis);

        assertThrows(IllegalArgumentException.class, () -> servicioJuego.realizarJugada("1-copas", null));

        verify(multijugador);
    }

    @Test
    void testRealizarJugadaValidoConCaptura() {
        // Setup completo
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall();

        List<String> cartasMesa = List.of("5-oros", "11-oros", "10-bastos", "11-bastos");
        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");

        multijugador.enviarCartasDeMesa(ID_SALA_VALIDO, CREADOR_VALIDO, cartasMesa);
        EasyMock.expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, CREADOR_VALIDO, cartasAna);
        EasyMock.expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, JUGADOR2_VALIDO, cartasLuis);
        EasyMock.expectLastCall();

        // Primera jugada: Ana juega 1-copas y captura 5-oros y 11-oros
        String cartaJugada = "1-copas";
        List<String> cartasCapturadas = List.of("5-oros", "11-oros");

        multijugador.enviarCartaJugada(ID_SALA_VALIDO, CREADOR_VALIDO, cartaJugada);
        EasyMock.expectLastCall();
        multijugador.enviarCartasCapturadas(ID_SALA_VALIDO, CREADOR_VALIDO, cartasCapturadas);
        EasyMock.expectLastCall();

        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();
        servicioJuego.repartirCartasInicial(cartasMesa, cartasAna, cartasLuis);
        servicioJuego.realizarJugada(cartaJugada, cartasCapturadas);

        verify(multijugador);
    }

    @Test
    void testRealizarJugadaValidoSinCaptura() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall();

        List<String> cartasMesa = List.of("5-oros", "11-oros", "10-bastos", "11-bastos");
        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");

        multijugador.enviarCartasDeMesa(ID_SALA_VALIDO, CREADOR_VALIDO, cartasMesa);
        EasyMock.expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, CREADOR_VALIDO, cartasAna);
        EasyMock.expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, JUGADOR2_VALIDO, cartasLuis);
        EasyMock.expectLastCall();

        // Turno 3: Ana juega 11-espadas sin capturar
        String cartaJugada = "11-espadas";
        List<String> cartasCapturadas = List.of();

        multijugador.enviarCartaJugada(ID_SALA_VALIDO, CREADOR_VALIDO, cartaJugada);
        EasyMock.expectLastCall();

        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();
        servicioJuego.repartirCartasInicial(cartasMesa, cartasAna, cartasLuis);
        servicioJuego.realizarJugada(cartaJugada, cartasCapturadas);

        verify(multijugador);
    }

    @Test
    void testRealizarJugadaNoValidoCartaNula() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall();

        List<String> cartasMesa = List.of("5-oros", "11-oros", "10-bastos", "11-bastos");
        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");

        multijugador.enviarCartasDeMesa(ID_SALA_VALIDO, CREADOR_VALIDO, cartasMesa);
        EasyMock.expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, CREADOR_VALIDO, cartasAna);
        EasyMock.expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, JUGADOR2_VALIDO, cartasLuis);
        EasyMock.expectLastCall();

        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();
        servicioJuego.repartirCartasInicial(cartasMesa, cartasAna, cartasLuis);

        assertThrows(IllegalArgumentException.class, () -> servicioJuego.realizarJugada(null, List.of()));

        verify(multijugador);
    }

    @Test
    void testRealizarJugadaNoValidoPartidaNoInicializada() {
        replay(multijugador);

        assertThrows(IllegalStateException.class, () -> servicioJuego.realizarJugada("1-copas", List.of()));

        verify(multijugador);
    }

    @Test
    void testRecibirJugadaRival() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall();
        List<String> cartasMesa = List.of("5-oros", "11-oros", "10-bastos", "11-bastos");
        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");

        multijugador.enviarCartasDeMesa(ID_SALA_VALIDO, CREADOR_VALIDO, cartasMesa);
        EasyMock.expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, CREADOR_VALIDO, cartasAna);
        EasyMock.expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, JUGADOR2_VALIDO, cartasLuis);
        EasyMock.expectLastCall();

        // Simular que Luis jugó su carta
        expect(multijugador.getCartaJugadaPorJugador(ID_SALA_VALIDO, JUGADOR2_VALIDO))
                .andReturn("7-oros");
        expect(multijugador.getCartasCapturadasPorJugador(ID_SALA_VALIDO, JUGADOR2_VALIDO))
                .andReturn(List.of("10-bastos"));

        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();
        servicioJuego.repartirCartasInicial(cartasMesa, cartasAna, cartasLuis);
        servicioJuego.recibirJugadaRival(JUGADOR2_VALIDO);

        verify(multijugador);
    }

    @Tag("Cobertura")
    @Test
    void testRecibirJugadaRivalValidoSinCaptura() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        expectLastCall();
        List<String> cartasMesa = List.of("5-oros", "11-oros", "10-bastos", "11-bastos");
        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("2-copas", "7-oros", "3-oros");

        multijugador.enviarCartasDeMesa(ID_SALA_VALIDO, CREADOR_VALIDO, cartasMesa);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, CREADOR_VALIDO, cartasAna);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, JUGADOR2_VALIDO, cartasLuis);
        expectLastCall();

        // El rival ha jugado "2-copas" y NO ha capturado cartas ->
        // getCartasCapturadasPorJugador devuelve null
        expect(multijugador.getCartaJugadaPorJugador(ID_SALA_VALIDO, JUGADOR2_VALIDO))
                .andReturn("2-copas");
        expect(multijugador.getCartasCapturadasPorJugador(ID_SALA_VALIDO, JUGADOR2_VALIDO))
                .andReturn(null);

        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();
        servicioJuego.repartirCartasInicial(cartasMesa, cartasAna, cartasLuis);

        Jugador j2 = servicioJuego.getGestorPartida().getJugador2();
        assertTrue(j2.getMano().contains(AdaptadorCartaJson.parse("2-copas")));

        servicioJuego.recibirJugadaRival(JUGADOR2_VALIDO);
        assertFalse(j2.getMano().contains(AdaptadorCartaJson.parse("2-copas")));
        assertFalse(j2.getCartas().contains(AdaptadorCartaJson.parse("2-copas")));
        ArrayList<Carta> mesaAfter = servicioJuego.obtenerCartasMesa();
        assertTrue(mesaAfter.contains(AdaptadorCartaJson.parse("2-copas")));

        verify(multijugador);
    }

    @Tag("Cobertura")
    @Test
    void testRecibirJugadaRivalRivalEsJugador1SinCapturasVacia() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        expectLastCall();

        // Estado inicial: mesa y manos con la carta que el rival (jugador1 =
        // CREADOR_VALIDO) va a jugar ("1-copas")
        List<String> cartasMesa = List.of("5-oros", "11-oros", "10-bastos", "11-bastos");
        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas"); // Ana es jugador1
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros"); // Luis es jugador2

        multijugador.enviarCartasDeMesa(ID_SALA_VALIDO, CREADOR_VALIDO, cartasMesa);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, CREADOR_VALIDO, cartasAna);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, JUGADOR2_VALIDO, cartasLuis);
        expectLastCall();

        expect(multijugador.getCartaJugadaPorJugador(ID_SALA_VALIDO, CREADOR_VALIDO))
                .andReturn("1-copas");
        expect(multijugador.getCartasCapturadasPorJugador(ID_SALA_VALIDO, CREADOR_VALIDO))
                .andReturn(List.of());

        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();
        servicioJuego.repartirCartasInicial(cartasMesa, cartasAna, cartasLuis);

        Jugador j1 = servicioJuego.getGestorPartida().getJugador1();
        assertTrue(j1.getMano().contains(AdaptadorCartaJson.parse("1-copas")));

        // Aplicar la jugada remota indicando que el rival es el jugador1
        // (CREADOR_VALIDO)
        servicioJuego.recibirJugadaRival(CREADOR_VALIDO);
        assertFalse(j1.getMano().contains(AdaptadorCartaJson.parse("1-copas")));
        ArrayList<Carta> mesaAfter = servicioJuego.obtenerCartasMesa();
        assertTrue(mesaAfter.contains(AdaptadorCartaJson.parse("1-copas")));
        assertFalse(j1.getCartas().contains(AdaptadorCartaJson.parse("1-copas")));

        verify(multijugador);
    }

    @Tag("Cobertura")
    @Test
    void testRecibirJugadaRivalNoValidoNoHayJugadaDisponible() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        expectLastCall();

        // Simular que no hay jugada (null).
        expect(multijugador.getCartaJugadaPorJugador(anyString(), eq(JUGADOR2_VALIDO)))
                .andReturn(null);
        expect(multijugador.getCartasCapturadasPorJugador(anyString(), eq(JUGADOR2_VALIDO)))
                .andReturn(null);

        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();

        assertThrows(IllegalStateException.class, () -> servicioJuego.recibirJugadaRival(JUGADOR2_VALIDO));

        verify(multijugador);
    }

    @Test
    void testRecibirJugadaRivalNoValidoNombreNulo() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall();
        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();

        assertThrows(IllegalArgumentException.class, () -> servicioJuego.recibirJugadaRival(null));

        verify(multijugador);
    }

    @Test
    void testRecibirJugadaRivalNoValidoPartidaNoInicializada() {
        replay(multijugador);

        assertThrows(IllegalStateException.class, () -> servicioJuego.recibirJugadaRival(JUGADOR2_VALIDO));

        verify(multijugador);
    }

    // ========== TESTS OBTENER CARTAS MESA Y JUGADOR ACTUAL ==========

    @Test
    void testObtenerCartasMesaValido() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall();

        List<String> cartasMesa = List.of("5-oros", "11-oros", "10-bastos", "11-bastos");
        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");

        multijugador.enviarCartasDeMesa(ID_SALA_VALIDO, CREADOR_VALIDO, cartasMesa);
        EasyMock.expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, CREADOR_VALIDO, cartasAna);
        EasyMock.expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, JUGADOR2_VALIDO, cartasLuis);
        EasyMock.expectLastCall();

        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();
        servicioJuego.repartirCartasInicial(cartasMesa, cartasAna, cartasLuis);

        ArrayList<Carta> mesa = servicioJuego.obtenerCartasMesa();
        assertEquals(4, mesa.size());

        verify(multijugador);
    }

    @Test
    void testObtenerJugadorActualValido() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        EasyMock.expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        EasyMock.expectLastCall();
        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();

        Jugador actual = servicioJuego.obtenerJugadorActual();
        assertEquals(CREADOR_VALIDO, actual.getNombre());

        verify(multijugador);
    }

    // ========== TESTS PARA COMPROBAR ESTADO LOCAL TRAS OPERACIONES ==========

    @Test
    void testRepartirCartasInicialEstadoLocal() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        expectLastCall();

        List<String> cartasMesa = List.of("5-oros", "11-oros", "10-bastos", "11-bastos");
        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");

        multijugador.enviarCartasDeMesa(ID_SALA_VALIDO, CREADOR_VALIDO, cartasMesa);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, CREADOR_VALIDO, cartasAna);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, JUGADOR2_VALIDO, cartasLuis);
        expectLastCall();

        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();
        servicioJuego.repartirCartasInicial(cartasMesa, cartasAna, cartasLuis);

        ArrayList<Carta> mesa = servicioJuego.obtenerCartasMesa();
        assertEquals(4, mesa.size(), "La mesa debe contener 4 cartas tras el reparto inicial");

        // Comprobar manos de los jugadores (orden respetado según reparto)
        Jugador j1 = servicioJuego.getGestorPartida().getJugador1();
        Jugador j2 = servicioJuego.getGestorPartida().getJugador2();

        List<Carta> expectedManoJ1 = new ArrayList<>();
        cartasAna.forEach(s -> expectedManoJ1.add(AdaptadorCartaJson.parse(s)));

        List<Carta> expectedManoJ2 = new ArrayList<>();
        cartasLuis.forEach(s -> expectedManoJ2.add(AdaptadorCartaJson.parse(s)));

        assertIterableEquals(expectedManoJ1, j1.getMano(), "La mano del jugador 1 no coincide con el reparto esperado");
        assertIterableEquals(expectedManoJ2, j2.getMano(), "La mano del jugador 2 no coincide con el reparto esperado");

        verify(multijugador);
    }

    @Test
    void testRealizarJugadaEstadoLocalConCaptura() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        expectLastCall();

        List<String> cartasMesa = List.of("5-oros", "11-oros", "10-bastos", "11-bastos");
        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");

        multijugador.enviarCartasDeMesa(ID_SALA_VALIDO, CREADOR_VALIDO, cartasMesa);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, CREADOR_VALIDO, cartasAna);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, JUGADOR2_VALIDO, cartasLuis);
        expectLastCall();

        String cartaJugada = "1-copas";
        List<String> cartasCapturadas = List.of("5-oros", "11-oros");
        multijugador.enviarCartaJugada(ID_SALA_VALIDO, CREADOR_VALIDO, cartaJugada);
        expectLastCall();
        multijugador.enviarCartasCapturadas(ID_SALA_VALIDO, CREADOR_VALIDO, cartasCapturadas);
        expectLastCall();

        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();
        servicioJuego.repartirCartasInicial(cartasMesa, cartasAna, cartasLuis);

        // Estado antes de la jugada
        Jugador j1 = servicioJuego.getGestorPartida().getJugador1();
        assertTrue(j1.getMano().contains(AdaptadorCartaJson.parse(cartaJugada)));

        servicioJuego.realizarJugada(cartaJugada, cartasCapturadas);

        // Estado tras la jugada
        // la carta jugada no debe estar en la mano
        assertFalse(j1.getMano().contains(AdaptadorCartaJson.parse(cartaJugada)));

        // las cartas capturadas + la carta jugada deben aparecer en las cartas
        // capturadas del jugador
        List<Carta> capturadasJ1 = j1.getCartas();
        assertTrue(capturadasJ1.contains(AdaptadorCartaJson.parse(cartaJugada)));
        for (String s : cartasCapturadas) {
            assertTrue(capturadasJ1.contains(AdaptadorCartaJson.parse(s)));
        }

        // la mesa debe haber perdido las cartas capturadas
        ArrayList<Carta> mesa = servicioJuego.obtenerCartasMesa();
        assertFalse(mesa.contains(AdaptadorCartaJson.parse("5-oros")));
        assertFalse(mesa.contains(AdaptadorCartaJson.parse("11-oros")));

        verify(multijugador);
    }

    @Test
    void testRecibirJugadaRivalEstadoLocal() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        expectLastCall();

        List<String> cartasMesa = List.of("5-oros", "11-oros", "10-bastos", "11-bastos");
        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");

        multijugador.enviarCartasDeMesa(ID_SALA_VALIDO, CREADOR_VALIDO, cartasMesa);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, CREADOR_VALIDO, cartasAna);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, JUGADOR2_VALIDO, cartasLuis);
        expectLastCall();

        expect(multijugador.getCartaJugadaPorJugador(ID_SALA_VALIDO, JUGADOR2_VALIDO)).andReturn("7-oros");
        expect(multijugador.getCartasCapturadasPorJugador(ID_SALA_VALIDO, JUGADOR2_VALIDO))
                .andReturn(List.of("10-bastos"));

        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();
        servicioJuego.repartirCartasInicial(cartasMesa, cartasAna, cartasLuis);

        Jugador j2 = servicioJuego.getGestorPartida().getJugador2();
        assertTrue(j2.getMano().contains(AdaptadorCartaJson.parse("7-oros")));

        servicioJuego.recibirJugadaRival(JUGADOR2_VALIDO);

        // Tras la jugada, la carta ya no está en la mano de Luis y debe aparecer en sus cartas
        assertFalse(j2.getMano().contains(AdaptadorCartaJson.parse("7-oros")));
        assertTrue(j2.getCartas().contains(AdaptadorCartaJson.parse("7-oros")));
        assertTrue(j2.getCartas().contains(AdaptadorCartaJson.parse("10-bastos")));

        verify(multijugador);
    }

    @Test
    void testRecibirJugadaRivalNoValidoCartaNoEnMano() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        expectLastCall();

        List<String> cartasMesa = List.of("5-oros", "11-oros", "10-bastos", "11-bastos");
        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");

        multijugador.enviarCartasDeMesa(ID_SALA_VALIDO, CREADOR_VALIDO, cartasMesa);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, CREADOR_VALIDO, cartasAna);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, JUGADOR2_VALIDO, cartasLuis);
        expectLastCall();

        // El rival (Luis) jugó "1-copas", pero esa carta está en mano de Ana, no de Luis -> inválido
        expect(multijugador.getCartaJugadaPorJugador(ID_SALA_VALIDO, JUGADOR2_VALIDO)).andReturn("1-copas");
        expect(multijugador.getCartasCapturadasPorJugador(ID_SALA_VALIDO, JUGADOR2_VALIDO)).andReturn(null);

        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();
        servicioJuego.repartirCartasInicial(cartasMesa, cartasAna, cartasLuis);

        assertThrows(IllegalStateException.class, () -> servicioJuego.recibirJugadaRival(JUGADOR2_VALIDO));

        verify(multijugador);
    }

    @Test
    void testRecibirJugadaRivalNoValidoCapturaCartaNoEnMesa() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        expectLastCall();

        List<String> cartasMesa = List.of("5-oros", "11-oros", "10-bastos", "11-bastos");
        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");

        multijugador.enviarCartasDeMesa(ID_SALA_VALIDO, CREADOR_VALIDO, cartasMesa);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, CREADOR_VALIDO, cartasAna);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, JUGADOR2_VALIDO, cartasLuis);
        expectLastCall();

        // Luis juega 7-oros y captura 1-copas (que NO está en la mesa) => inválido
        expect(multijugador.getCartaJugadaPorJugador(ID_SALA_VALIDO, JUGADOR2_VALIDO)).andReturn("7-oros");
        expect(multijugador.getCartasCapturadasPorJugador(ID_SALA_VALIDO, JUGADOR2_VALIDO))
                .andReturn(List.of("1-copas"));

        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();
        servicioJuego.repartirCartasInicial(cartasMesa, cartasAna, cartasLuis);

        assertThrows(IllegalStateException.class, () -> servicioJuego.recibirJugadaRival(JUGADOR2_VALIDO));

        verify(multijugador);
    }

    @Test
    void testRecibirJugadaRivalNoValidoSumaNoQuince() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        expectLastCall();

        List<String> cartasMesa = List.of("5-oros", "11-oros", "10-bastos", "11-bastos");
        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");

        multijugador.enviarCartasDeMesa(ID_SALA_VALIDO, CREADOR_VALIDO, cartasMesa);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, CREADOR_VALIDO, cartasAna);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, JUGADOR2_VALIDO, cartasLuis);
        expectLastCall();

        // Luis juega 7-oros y captura 11-bastos -> suma 9 != 15 => inválido
        expect(multijugador.getCartaJugadaPorJugador(ID_SALA_VALIDO, JUGADOR2_VALIDO)).andReturn("7-oros");
        expect(multijugador.getCartasCapturadasPorJugador(ID_SALA_VALIDO, JUGADOR2_VALIDO))
                .andReturn(List.of("11-bastos"));

        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();
        servicioJuego.repartirCartasInicial(cartasMesa, cartasAna, cartasLuis);
        assertThrows(IllegalStateException.class, () -> servicioJuego.recibirJugadaRival(JUGADOR2_VALIDO));

        verify(multijugador);
    }

    @Test
    void testRecibirJugadaRivalNoValidoSumaNoQuinceOtro() {
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        expectLastCall();

        List<String> cartasMesa = List.of("5-oros", "11-oros", "12-bastos", "11-bastos");
        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");

        multijugador.enviarCartasDeMesa(ID_SALA_VALIDO, CREADOR_VALIDO, cartasMesa);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, CREADOR_VALIDO, cartasAna);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, JUGADOR2_VALIDO, cartasLuis);
        expectLastCall();

        // Luis juega 3-oros y captura 12-bastos -> suma 9 != 15 => inválido
        expect(multijugador.getCartaJugadaPorJugador(ID_SALA_VALIDO, JUGADOR2_VALIDO)).andReturn("3-oros");
        expect(multijugador.getCartasCapturadasPorJugador(ID_SALA_VALIDO, JUGADOR2_VALIDO))
                .andReturn(List.of("12-bastos"));

        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();
        servicioJuego.repartirCartasInicial(cartasMesa, cartasAna, cartasLuis);
        assertThrows(IllegalStateException.class, () -> servicioJuego.recibirJugadaRival(JUGADOR2_VALIDO));

        verify(multijugador);
    }

    @Test
    void testRecibirJugadaRivalNoValidoJugadorNoParticipaEnPartida() {
        // crear sala con Ana y Luis
        multijugador.crearSala(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        expectLastCall();
        multijugador.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        expectLastCall();

        List<String> cartasMesa = List.of("5-oros", "11-oros", "10-bastos", "11-bastos");
        List<String> cartasAna = List.of("1-copas", "4-copas", "11-espadas");
        List<String> cartasLuis = List.of("7-oros", "2-copas", "3-oros");

        multijugador.enviarCartasDeMesa(ID_SALA_VALIDO, CREADOR_VALIDO, cartasMesa);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, CREADOR_VALIDO, cartasAna);
        expectLastCall();
        multijugador.enviarCartasDeJugador(ID_SALA_VALIDO, CREADOR_VALIDO, JUGADOR2_VALIDO, cartasLuis);
        expectLastCall();

        replay(multijugador);

        servicioJuego.crearPartida(ID_SALA_VALIDO, CREADOR_VALIDO, false);
        servicioJuego.unirseASala(ID_SALA_VALIDO, JUGADOR2_VALIDO, null);
        servicioJuego.inicializarPartida();
        servicioJuego.repartirCartasInicial(cartasMesa, cartasAna, cartasLuis);

        // Ejecutar: intentar recibir jugada de un jugador que NO está en la partida (Carlos)
        assertThrows(IllegalArgumentException.class, () -> servicioJuego.recibirJugadaRival("Carlos"));

        verify(multijugador);
    }

}